package wiki.scene.cryption.core.sm2;

import wiki.scene.cryption.EncryptionManager;
import wiki.scene.cryption.core.AbstractCoder;

public class SM2Helper {

    private static class SM2HelperHolder {
        private static final SM2Helper instance = new SM2Helper();
    }

    private SM2Helper() {
    }

    public static SM2Helper getInstance() {
        return SM2HelperHolder.instance;
    }

    public String encode(String data, String publicKey) {
        AbstractCoder cipher = EncryptionManager.getCipher(EncryptionManager.Model.SM2);
        return cipher.simpleEnCode(data, publicKey);
    }

}
