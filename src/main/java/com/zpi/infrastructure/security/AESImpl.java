package com.zpi.infrastructure.security;

import org.springframework.stereotype.Component;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

@Component
public class AESImpl implements AES {
    private  final String ALGO = "AES";
    public String encrypt(String data) {
        if(data==null)
            return "";
        Key key = generateKey();
        try {
            Cipher cipher = Cipher.getInstance(ALGO);
            cipher.init(Cipher.ENCRYPT_MODE, key);
            byte[] encVal = cipher.doFinal(data.getBytes());
            return Base64.getEncoder().encodeToString(encVal);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException e){
            System.out.println(e);
        }
        return data;
    }

    public String decrypt(String encryptedData)  {
        if(encryptedData==null)
            return "";
        Key key = generateKey();
        try {
            Cipher c = Cipher.getInstance(ALGO);
            c.init(Cipher.DECRYPT_MODE, key);
            byte[] decodedValue = Base64.getDecoder().decode(encryptedData);
            byte[] decValue = c.doFinal(decodedValue);
            return new String(decValue);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException e){
            System.out.println(e);
        }
        return encryptedData;
    }

    private Key generateKey(){
        String secretKey = "shSHs321ecuredsadD51HdsaACQLOA33sharedsadD51HdsaACQLOA33ACO3482JJ12SHs321ecudsaredaredsadD51HdsaACQLOA33ACO3482JJ12sadD51HdsaACQLOA33ACO3482JJ123DEF1LONGA0AENUGH4dDSAACO348shSHs321ecuredsadD51HdsaACQLOA33ACO3482JJ123DEF1LONGA0AENUGH4dDSA2JJ123DEF1LONGA0AENUGH4dDSA-dA58i1A123FAHFh";
        return new SecretKeySpec(secretKey.getBytes(), ALGO);
    }
}