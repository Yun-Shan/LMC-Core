package org.yunshanmc.lmc.core.gui.sign;

import org.yunshanmc.lmc.core.util.PlatformUtils;

import java.util.function.Consumer;

public class SignHelper {

    private static SignEditorProvider<Object> editorProvider;

    @SuppressWarnings("unchecked")
    public static void setEditorProvider(SignEditorProvider editorProvider) {
        if (SignHelper.editorProvider != null) {
            throw new IllegalStateException();
        }
        SignHelper.editorProvider = editorProvider;
    }

    public static void openSignEditor(Object player, String[] lines, Consumer<String[]> callback) {
        if (editorProvider == null) {
            throw new UnsupportedOperationException("SignEditor Unsupported at " + PlatformUtils.getPlatform());
        }
        editorProvider.openSignEditor(player, lines, callback);
    }

    public interface SignEditorProvider<T> {
        void openSignEditor(T player, String[] lines, Consumer<String[]> callback);
    }
}
