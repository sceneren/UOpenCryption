package wiki.scene.cryption.core.sm2;

import android.text.TextUtils;

import org.bouncycastle.crypto.generators.ECKeyPairGenerator;
import org.bouncycastle.crypto.params.ECDomainParameters;
import org.bouncycastle.crypto.params.ECKeyGenerationParameters;
import org.bouncycastle.math.ec.ECCurve;
import org.bouncycastle.math.ec.ECPoint;

import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;

import wiki.scene.cryption.core.AbstractCoder;
import wiki.scene.cryption.utils.HexUtils;

/**
 * Created by fplei on 2018/9/21.
 */
public class Sm2Kit extends AbstractCoder {
    //正式参数
    public static String[] ecc_param = {
            "FFFFFFFEFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF00000000FFFFFFFFFFFFFFFF",
            "FFFFFFFEFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF00000000FFFFFFFFFFFFFFFC",
            "28E9FA9E9D9F5E344D5A9E4BCF6509A7F39789F515AB8F92DDBCBD414D940E93",
            "FFFFFFFEFFFFFFFFFFFFFFFFFFFFFFFF7203DF6B21C6052B53BBF40939D54123",
            "32C4AE2C1F1981195F9904466A39C9948FE30BBFF2660BE1715A4589334C74C7",
            "BC3736A2F4F6779C59BDCEE36B692153D0A9877CC62A474002DF32E52139F0A0"
    };
    public final BigInteger ecc_p;
    public final BigInteger ecc_a;
    public final BigInteger ecc_b;
    public final BigInteger ecc_n;
    public final BigInteger ecc_gx;
    public final BigInteger ecc_gy;
    public final ECCurve ecc_curve;
    public final ECPoint ecc_point_g;
    public final ECDomainParameters ecc_bc_spec;
    public final ECKeyPairGenerator ecc_key_pair_generator;

    //    public final ECFieldElement ecc_gx_fieldelement;
//    public final ECFieldElement ecc_gy_fieldelement;
    public Sm2Kit() {
        this.ecc_p = new BigInteger(ecc_param[0], 16);
        this.ecc_a = new BigInteger(ecc_param[1], 16);
        this.ecc_b = new BigInteger(ecc_param[2], 16);
        this.ecc_n = new BigInteger(ecc_param[3], 16);
        this.ecc_gx = new BigInteger(ecc_param[4], 16);
        this.ecc_gy = new BigInteger(ecc_param[5], 16);
//        this.ecc_gx_fieldelement = new ECFieldElement.Fp(this.ecc_p, this.ecc_gx);
//        this.ecc_gy_fieldelement = new ECFieldElement.Fp(this.ecc_p, this.ecc_gy);
        this.ecc_curve = new ECCurve.Fp(this.ecc_p, this.ecc_a, this.ecc_b);
//        this.ecc_point_g = new ECPoint.Fp(this.ecc_curve, this.ecc_gx_fieldelement, this.ecc_gy_fieldelement);
        this.ecc_point_g = this.ecc_curve.createPoint(ecc_gx, ecc_gy);
        this.ecc_bc_spec = new ECDomainParameters(this.ecc_curve, this.ecc_point_g, this.ecc_n);
        ECKeyGenerationParameters ecc_ecgenparam;
        ecc_ecgenparam = new ECKeyGenerationParameters(this.ecc_bc_spec, new SecureRandom());
        this.ecc_key_pair_generator = new ECKeyPairGenerator();
        this.ecc_key_pair_generator.init(ecc_ecgenparam);
    }

    /**
     * 加密
     *
     * @param value 需要加密的数据
     * @param key   密钥 (密钥需要16进制字符串,其他请使用byte[])
     */
    @Override
    public String simpleEnCode(String value, String key) {
        if (TextUtils.isEmpty(value) || TextUtils.isEmpty(key)) {
            return null;
        }
        try {
            byte[] keyBytes = HexUtils.hexStringToBytes(key);
            byte[] result = enCode(value.getBytes(StandardCharsets.UTF_8), keyBytes);
            if (result != null) {
                return HexUtils.byteToHex(result);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public byte[] enCode(byte[] value, byte[] key) {
        if (key == null || value == null) {
            return null;
        }
        byte[] source = new byte[value.length];
        System.arraycopy(value, 0, source, 0, value.length);
        Cipher cipher = new Cipher();
        ECPoint userKey = this.ecc_curve.decodePoint(key);
        ECPoint c1 = cipher.Init_enc(this, userKey);
        cipher.Encrypt(source);
        byte[] encode = c1.getEncoded(false);
        byte[] c3 = new byte[32];
        cipher.doFinal(c3);
        ByteBuffer byteBuffer = ByteBuffer.allocate(encode.length + source.length + c3.length);
        byteBuffer.put(encode);
        byteBuffer.put(source);
        byteBuffer.put(c3);
        //C1 C2 C3拼装成加密字串
//        return Utils.byteToHex(c1.getEncoded()) + Utils.byteToHex(source) + Utils.byteToHex(c3);
        return byteBuffer.array();
    }

    /**
     * 解密
     *
     * @param value 需要解密数据(默认传入为16进制数据)
     * @param key   密钥（需要16进制字符串，其他编码请使用byte[]）
     */
    @Override
    public String simpleDeCode(String value, String key) {
        if (TextUtils.isEmpty(value) || TextUtils.isEmpty(key)) {
            return null;
        }
        byte[] datas = HexUtils.hexStringToBytes(value);
        byte[] keyBytes = HexUtils.hexStringToBytes(key);
        return new String(deCode(datas, keyBytes));
    }

    @Override
    public byte[] deCode(byte[] value, byte[] key) {
        if (value == null || key == null) {
            return null;
        }
        //加密字节数组转换为十六进制的字符串 长度变为value.length * 2
        String data = HexUtils.byteToHex(value);
        // 分解加密字串
        //（C1 = C1标志位2位 + C1实体部分128位 = 130）
        //（C2 = encryptedData.length * 2 - C1长度  - C2长度）
        //（C3 = C3实体部分64位  = 64）
        byte[] c1Bytes = HexUtils.hexToByte(data.substring(0, 130));
        int c2Len = value.length - 97;
        byte[] c2 = HexUtils.hexToByte(data.substring(130, 130 + 2 * c2Len));
        byte[] c3 = HexUtils.hexToByte(data.substring(130 + 2 * c2Len, 194 + 2 * c2Len));

        BigInteger userD = new BigInteger(1, key);

        //通过C1实体字节来生成ECPoint
        ECPoint c1 = ecc_curve.decodePoint(c1Bytes);
        Cipher cipher = new Cipher();
        cipher.Init_dec(userD, c1);
        cipher.Decrypt(c2);
        cipher.doFinal(c3);
        //返回解密结果
        return c2;
    }

}
