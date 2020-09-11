package org.yunshanmc.lmc.core.updater.driver;

import com.google.common.io.ByteStreams;
import org.yunshanmc.lmc.core.updater.Version;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.BiPredicate;
import java.util.function.Predicate;
import java.util.regex.Pattern;

/**
 * @author Yun Shan
 */
@SuppressWarnings("unchecked")
public class GithubUpdater extends AbstractUpdater {

    private final String userName;
    private final String repoName;
    private final BiPredicate<String, String> versionNameChecker;
    private final Predicate<String> assetNameChecker;

    public GithubUpdater(String userName, String repoName, BiPredicate<String, String> versionNameChecker, Predicate<String> assetNameChecker) {
        this.userName = userName;
        this.repoName = repoName;
        this.versionNameChecker = versionNameChecker;
        this.assetNameChecker = assetNameChecker;
    }

    @Override
    public Version checkVersion(String oldVersion) {
        Map<String, Object> version;
        try {
            version = getLatestVersion();
        } catch (Exception e) {
            // 网络错误
            return null;
        }
        String name = (String) version.get("name");
        if (!this.versionNameChecker.test(oldVersion, name)) {
            return new Version(oldVersion, null);
        }
        List<Map<String, Object>> assets = (List<Map<String, Object>>) version.get("assets");
        String downloadUrl = null;
        for (Map<String, Object> asset : assets) {
            String assetName = (String) asset.get("name");
            if (this.assetNameChecker.test(assetName)) {
                downloadUrl = (String) asset.get("browser_download_url");
                break;
            }
        }
        return new Version(name, downloadUrl);
    }

    private Map<String, Object> getLatestVersion() throws IOException, ScriptException {
        String url = MessageFormat.format("https://api.github.com/repos/{0}/{1}/releases", this.userName, this.repoName);
        InputStream inputStream = new URL(url).openConnection().getInputStream();
        byte[] bytes = ByteStreams.toByteArray(inputStream);
        String json = new String(bytes, StandardCharsets.UTF_8);
        ScriptEngine engine = new ScriptEngineManager().getEngineByName("js");
        engine.eval("var obj = " + json);
        Object obj = engine.get("obj");
        List<Object> list = transformList(obj);
        list.forEach(o -> {
            Map map = (Map) o;
            map.put("created_at", LocalDateTime.parse((CharSequence) map.get("created_at"), DateTimeFormatter.ISO_OFFSET_DATE_TIME));
            map.put("published_at", LocalDateTime.parse((CharSequence) map.get("published_at"), DateTimeFormatter.ISO_OFFSET_DATE_TIME));
        });
        list.sort((o1, o2) -> {
            Map map1 = (Map) o1;
            Map map2 = (Map) o2;
            LocalDateTime time1 = (LocalDateTime) map1.get("published_at");
            LocalDateTime time2 = (LocalDateTime) map2.get("published_at");
            // 大到小排序
            return time2.compareTo(time1);
        });
        return (Map<String, Object>) list.get(0);
    }

    private List<Object> transformList(Object obj) {
        ArrayList<Object> list = new ArrayList<>(((Map) obj).values());
        for (int i = 0; i < list.size(); i++) {
            Object o = list.get(i);
            if (o instanceof Map && !o.getClass().equals(HashMap.class)) {
                list.set(i, transformListOrMap(o));
            }
        }
        return list;
    }

    private Map<Object, Object> transformMap(Object obj) {
        HashMap<Object, Object> map = new HashMap<>(((Map) obj));
        Set<Map.Entry<Object, Object>> set = map.entrySet();
        for (Map.Entry entry : set) {
            Object val = entry.getValue();
            if (val instanceof Map && !val.getClass().equals(HashMap.class)) {
                entry.setValue(transformListOrMap(val));
            }
        }
        return map;
    }

    private static final Pattern INT_PATTERN = Pattern.compile("^[0-9]{1,11}$");

    private Object transformListOrMap(Object obj) {
        Set keys = ((Map) obj).keySet();
        int counter = 0;
        boolean isList = true;
        for (Object key : keys) {
            if (key instanceof String) {
                String strKey = (String) key;
                if (INT_PATTERN.matcher(strKey).matches()) {
                    int i;
                    try {
                        i = Integer.parseInt((String) key, 10);
                    } catch (NumberFormatException e) {
                        isList = false;
                        break;
                    }
                    if (i == counter) {
                        counter++;
                        continue;
                    }
                }
            }
            isList = false;
            break;
        }
        return isList ? transformList(obj) : transformMap(obj);
    }
}
