package com.bakkenbaeck.token;

import com.bakkenbaeck.token.crypto.HDWallet;
import com.bakkenbaeck.token.model.network.*;
import com.bakkenbaeck.token.network.BalanceService;
import retrofit2.Response;

import java.io.IOException;


public class EthService {
    private final BalanceService balanceService;
    private final HDWallet wallet;

    public EthService(HDWallet wallet) {
        this.wallet = wallet;
        this.balanceService = new BalanceService(wallet);
    }

    public static byte[] hexStringToByteArray(String s) {
        byte[] b = new byte[s.length() / 2];
        for (int i = 0; i < b.length; i++) {
            int index = i * 2;
            int v = Integer.parseInt(s.substring(index, index + 2), 16);
            b[i] = (byte) v;
        }
        return b;
    }

    public SentTransaction sendEth(String to, String value) throws IOException {
        String from = wallet.getWalletAddress();
        TransactionRequest request = new TransactionRequest()
            .setToAddress(to)
            .setFromAddress(from)
            .setValue(value);
        Response<UnsignedTransaction> response = balanceService.getApi().createTransaction(request).execute();
        if (response.body() != null) {
            String utx = response.body().getTransaction();
            final long ts = balanceService.getApi().getTimestamp().execute().body().get();
            String signature = this.wallet.signTransaction(utx);
            final SignedTransaction signedTransaction = new SignedTransaction()
                    .setEncodedTransaction(utx)
                    .setSignature(signature);
            SentTransaction tx = balanceService.getApi().sendSignedTransaction(ts, signedTransaction).execute().body();
            return tx;
        }
        return null;
    }

}