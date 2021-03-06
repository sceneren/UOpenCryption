package com.lfp.uopencryption;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.swt.set.key.SwtKeyUtils;

import java.util.logging.Logger;

import wiki.scene.cryption.EncryptionManager;
import wiki.scene.cryption.core.AbstractCoder;
import wiki.scene.cryption.core.dsa.DSAKeyHelper;
import wiki.scene.cryption.core.rsa.RsaKeyHelper;
import wiki.scene.cryption.core.sm2.SM2Helper;
import wiki.scene.cryption.utils.HexUtils;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private EditText et_encryption;
    private TextView text_result;
    private Button bt_sm2, bt_sm3, bt_sm4, bt_trides, bt_onedes, bt_aes, bt_dsa, bt_rsa, bt_sha1, bt_md5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        et_encryption = findViewById(R.id.et_encryption);
        text_result = findViewById(R.id.text_result);
        bt_sm2 = findViewById(R.id.bt_sm2);
        bt_sm2.setOnClickListener(this);
        bt_sm3 = findViewById(R.id.bt_sm3);
        bt_sm3.setOnClickListener(this);
        bt_sm4 = findViewById(R.id.bt_sm4);
        bt_sm4.setOnClickListener(this);
        bt_trides = findViewById(R.id.bt_trides);
        bt_trides.setOnClickListener(this);
        bt_onedes = findViewById(R.id.bt_onedes);
        bt_onedes.setOnClickListener(this);
        bt_aes = findViewById(R.id.bt_aes);
        bt_aes.setOnClickListener(this);
        bt_dsa = findViewById(R.id.bt_dsa);
        bt_dsa.setOnClickListener(this);
        bt_rsa = findViewById(R.id.bt_rsa);
        bt_rsa.setOnClickListener(this);
        bt_sha1 = findViewById(R.id.bt_sha1);
        bt_sha1.setOnClickListener(this);
        bt_md5 = findViewById(R.id.bt_md5);
        bt_md5.setOnClickListener(this);
        TextView tvPrivateKey = findViewById(R.id.tvPrivateKey);
        tvPrivateKey.setText("debug:" + SwtKeyUtils.getSM2TestPublicKey() + "\n"
                + "release:" + SwtKeyUtils.getSM2PublicKey());

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_sm2:
                optSM2();
                break;
            case R.id.bt_sm3:
                digstSM3();
                break;
            case R.id.bt_sm4:
                optSM4();
                break;
            case R.id.bt_trides:
                optTrisDes();
                break;
            case R.id.bt_onedes:
                optDes();
                break;
            case R.id.bt_aes:
                optAes();
                break;
            case R.id.bt_dsa:
                optDsa();
                break;
            case R.id.bt_rsa:
                optRsa();
                break;
            case R.id.bt_sha1:
                optMacSha1();
                break;
            case R.id.bt_md5:
                optMD5();
                break;
        }
    }

    public void clear(View v) {
        text_result.setText("");
        EncryptionManager.reStoreCipher();
    }


    /**
     * ??????SM2??????????????????????????????RAS????????????????????????????????????????????????
     */
    private void optSM2() {
        String privateKeyStr = SwtKeyUtils.getSM2PublicKey();
        Log.e("xx", privateKeyStr);
        String str="{\"headerInfo\":{\"areaType\":\"1\",\"osType\":1,\"token\":\"64acf01a30c2e80321a111036c32e3d0\",\"userId\":\"751185878482681856\"},\"params\":{}}";
        String encodeStr = SM2Helper.getInstance().encode(str, privateKeyStr);

        AbstractCoder cipher = EncryptionManager.getCipher(EncryptionManager.Model.SM2);
        String decodeStr = cipher.simpleDeCode(encodeStr, "00890C3C4389AA3B23D48B2C62277727DCA7510879C534A205FCBF529ED1BD658D");
        Log.e("??????:", encodeStr);
        Log.e("??????:", decodeStr);


//        String plainText = et_encryption.getText().toString();
//        if(TextUtils.isEmpty(plainText)){
//            return;
//        }
//        long startTime=System.currentTimeMillis();
//        AbstractCoder cipher=EncryptionManager.getCipher(EncryptionManager.Model.SM2);
//        SM2KeyHelper.KeyPair keyPair= SM2KeyHelper.generateKeyPair((Sm2Kit)cipher);
//        //???????????????
//        String privateKeyHex= keyPair.getPrivateKey();
//        String publicKeyHex=keyPair.getPublicKey();
//        Log.e("privateKeyHex",privateKeyHex);
//        Log.e("publicKeyHex",publicKeyHex);
//        Log.i("lfp","privateKeyHex.length="+privateKeyHex.length()+",publicKeyHex.length="+publicKeyHex.length());
//        try{
//            text_result.append("\nSM2??????: \n");
//            String cipherText=cipher.simpleEnCode(plainText,publicKeyHex);
//            long encryEndTime=System.currentTimeMillis();
//            Log.i("lfp","cipherText="+cipherText);
//            text_result.append("??????:"+cipherText+"\n?????????"+(encryEndTime-startTime)+"??????");
//            text_result.append("\n");
//            plainText=cipher.simpleDeCode(cipherText,privateKeyHex);
//            long dncryEndTime=System.currentTimeMillis();
//            text_result.append("??????: \n");
//            text_result.append("??????:"+plainText+"\n?????????"+(dncryEndTime-encryEndTime)+"??????");
//        }catch (Exception e){
//            e.printStackTrace();
//        }
    }

    /**
     * ?????????????????????MD5/SHA1?????????
     */
    private void digstSM3() {
        String plainText = et_encryption.getText().toString();
        if (TextUtils.isEmpty(plainText)) {
            return;
        }
        long startTime = System.currentTimeMillis();
        AbstractCoder cipher = EncryptionManager.getCipher(EncryptionManager.Model.SM3);
        String s = cipher.digestSignature(plainText, null);
        //SM3?????????????????????????????????
        EncryptionManager.reStoreCipher();
        long endTime = System.currentTimeMillis();
        text_result.append("\n??????: \n");
        text_result.append("??????: " + s + "\n?????????????????????" + (endTime - startTime) + "??????");
    }

    /**
     * ?????????????????????3DES??????3DES??????
     */
    private void optSM4() {
        String plainText = et_encryption.getText().toString();
        if (TextUtils.isEmpty(plainText)) {
            return;
        }
        long startTime = System.currentTimeMillis();
        AbstractCoder cipher = EncryptionManager.getCipher(EncryptionManager.Model.SM4);
        String key = HexUtils.byteToHex("JeF8U9wHFOMfs2Y8".getBytes());
        text_result.append("\nSM4-ECB????????????: \n");
        String cipherText = cipher.simpleEnCode(plainText, key);
        long endTime = System.currentTimeMillis();
        text_result.append("??????: " + cipherText + "\nECB??????????????????:" + (endTime - startTime) + "??????");
        text_result.append("\n");
        text_result.append("SM4-ECB????????????: \n");
        plainText = cipher.simpleDeCode(cipherText, key);
        long decryEndTime = System.currentTimeMillis();
        text_result.append("??????: " + plainText + "\nECB??????????????????:" + (decryEndTime - endTime) + "??????");
        text_result.append("\n");
    }

    /**
     * 3des
     */
    private void optTrisDes() {
        String value = et_encryption.getText().toString();
        if (TextUtils.isEmpty(value)) {
            return;
        }
        String key = "alkjhstr84735281986bdas5";
        long startTime = System.currentTimeMillis();
        AbstractCoder abstractCoder = EncryptionManager.getCipher(EncryptionManager.Model.TRIDES);
//        TriDesKit triDesKit=new TriDesKit();
        String encryResult = HexUtils.byteToHex(abstractCoder.enCode(value.getBytes(), key.getBytes()));
        long endTime = System.currentTimeMillis();
        text_result.append("3DES??????:\n");
        text_result.append("??????: " + encryResult + "\n??????:" + (endTime - startTime) + "??????");
        text_result.append("\n");
        text_result.append("3DES??????:\n");
        String DencryResult = new String(abstractCoder.deCode(HexUtils.hexStringToBytes(encryResult), key.getBytes()));
        long endTime1 = System.currentTimeMillis();
        text_result.append("??????: " + DencryResult + "\n??????:" + (endTime1 - endTime) + "??????");
        text_result.append("\n");
    }

    /**
     * aes
     */
    private void optAes() {
        String value = et_encryption.getText().toString();
        if (TextUtils.isEmpty(value)) {
            return;
        }
        long startTime = System.currentTimeMillis();
        String rule = "9879238423";
        AbstractCoder abstractCoder = EncryptionManager.getCipher(EncryptionManager.Model.AES);
        String ecnryResult = abstractCoder.simpleEnCode(value, rule);
        long endTime = System.currentTimeMillis();
        text_result.append("AES??????:\n");
        text_result.append("??????: " + ecnryResult + "\n??????:" + (endTime - startTime) + "??????");
        text_result.append("\n");
        String decryResult = abstractCoder.simpleDeCode(ecnryResult, rule);
        long endTime1 = System.currentTimeMillis();
        text_result.append("AES??????:\n");
        text_result.append("??????: " + decryResult + "\n??????:" + (endTime1 - endTime) + "??????");
        text_result.append("\n");
    }

    /**
     * rsa
     */
    private void optRsa() {
        String value = et_encryption.getText().toString();
        if (TextUtils.isEmpty(value)) {
            return;
        }
        RsaKeyHelper.KeyPass keyPass = RsaKeyHelper.generateKeyPair();
        long startTime = System.currentTimeMillis();
//        RsaKit rsaKit=new RsaKit();
        AbstractCoder abstractCoder = EncryptionManager.getCipher(EncryptionManager.Model.RSA);
        String encryResult = abstractCoder.simpleEnCode(value, keyPass.getPublicKeyHex());
        long endTime = System.currentTimeMillis();
        text_result.append("RSA??????:\n");
        text_result.append("??????: " + encryResult + "\n??????:" + (endTime - startTime) + "??????");
        text_result.append("\n");
        try {
            String decryResult = abstractCoder.simpleDeCode(encryResult, keyPass.getPrivateKeyHex());
            long endTime1 = System.currentTimeMillis();
            text_result.append("RAS??????:\n");
            text_result.append("??????: " + decryResult + "\n??????:" + (endTime1 - endTime) + "??????");
            text_result.append("\n");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //?????????????????????????????????
    private void optDsa() {
        String value = et_encryption.getText().toString();
        if (TextUtils.isEmpty(value)) {
            return;
        }
        long startTime = System.currentTimeMillis();
        String seed = "akjh93124kjasfwe23423sd323";
        DSAKeyHelper.KeyPass keyPass = DSAKeyHelper.genKeyPair(seed);
        Log.i("lfp", "publicKeyHex" + keyPass.getPublicKeyHex());
        Log.i("lfp", "privateKeyHex" + keyPass.getPrivateKeyHex());
//        DSAKit dsaKit=new DSAKit();
        AbstractCoder abstractCoder = EncryptionManager.getCipher(EncryptionManager.Model.DSA);
        String sign = abstractCoder.digestSignature(value, keyPass.getPrivateKeyHex());
        long endTime = System.currentTimeMillis();
        text_result.append("DSA??????:\n");
        text_result.append(sign + "\n");
        boolean flag = abstractCoder.verifyWithDSA(value.getBytes(), sign, HexUtils.hexStringToBytes(keyPass.getPublicKeyHex()));
        text_result.append("\n" + "?????????" + (endTime - startTime) + "??????\n");
        text_result.append("???????????????" + flag);
        text_result.append("\n");
    }

    private void optDes() {
        String plainText = et_encryption.getText().toString();
        if (TextUtils.isEmpty(plainText)) {
            return;
        }
        String key = "12345678";
        AbstractCoder abstractCoder = EncryptionManager.getCipher(EncryptionManager.Model.DES);
        String result = abstractCoder.simpleEnCode(plainText, key);
        text_result.append("\nDES??????: \n");
        text_result.append(result);
        text_result.append("\nDES??????: \n");
        result = abstractCoder.simpleDeCode(result, key);
        text_result.append(result + "\n");
    }

    private void optMacSha1() {
        String plainText = et_encryption.getText().toString();
        if (TextUtils.isEmpty(plainText)) {
            return;
        }
        AbstractCoder abstractCoder = EncryptionManager.getCipher(EncryptionManager.Model.HMAC_SHA1);
        String key = "handbabala";
        String signatrue = abstractCoder.digestSignature(plainText, key);
        text_result.append("\nHMAC_SHA1??????: \n");
        text_result.append(signatrue + "\n");
    }

    private void optMD5() {
        String plainText = et_encryption.getText().toString();
        if (TextUtils.isEmpty(plainText)) {
            return;
        }
        AbstractCoder abstractCoder = EncryptionManager.getCipher(EncryptionManager.Model.MD5);
        String md5 = abstractCoder.digestSignature(plainText, null);
        text_result.append("\nMD5??????: \n");
        text_result.append(md5 + "\n");
    }
}
