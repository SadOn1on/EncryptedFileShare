package by.client.RSA;

import java.math.BigInteger;
import java.security.InvalidParameterException;
import java.util.Random;

import static java.math.BigInteger.ONE;

public class RSA {
    private BigInteger p;
    private BigInteger q;
    private BigInteger n;
    private BigInteger phi;
    private BigInteger e;
    private BigInteger d;


    public RSA(int l) {
        gen(l);
    }

    public RSA(BigInteger q, BigInteger p, BigInteger e, BigInteger d) throws InvalidParameterException {

        if (!e.testBit(0)) {
            throw new InvalidParameterException("Invalid e");
        }
        if (!p.isProbablePrime(10)) {
            throw new InvalidParameterException("Invalid p");
        }
        if (!q.isProbablePrime(10)) {
            throw new InvalidParameterException("Invalid q");
        }
        if (!Utils.extendedEuclid(e, p.subtract(ONE))[0].equals(ONE)) {
            throw new InvalidParameterException("Invalid e p pair");
        }
        if (!Utils.extendedEuclid(e, q.subtract(ONE))[0].equals(ONE)) {
            throw new InvalidParameterException("Invalid e q pair");
        }
        if (!e.multiply(d).mod(q.subtract(ONE).multiply(p.subtract(ONE))).equals(ONE)) {
            throw new InvalidParameterException("Invalid e d pair");
        }


        this.q = q;
        this.p = p;
        this.e = e;
        this.d = d;
        this.n = p.multiply(q);
        this.phi = q.subtract(ONE).multiply(p.subtract(ONE));
    }

    private void gen(int l) {
        Random random = new Random();
        e = new BigInteger(l, random);

        if (!e.testBit(0)) e = e.flipBit(0);

        p = Utils.genPrime(l / 2);
        while (!Utils.extendedEuclid(e, p.subtract(ONE))[0].equals(ONE)) p = Utils.genPrime(l / 2);
        q = Utils.genPrime(l / 2 + l % 2);
        while (!Utils.extendedEuclid(e, q.subtract(ONE))[0].equals(ONE)) q = Utils.genPrime(l / 2 + l % 2);
        n = p.multiply(q);

        phi = (p.subtract(ONE)).multiply(q.subtract(ONE));

        d = Utils.extendedEuclid(e, phi)[1];
    }

    public BigInteger getN() {
        return n;
    }

    public BigInteger getE() {
        return e;
    }

    // Encrypt message
    public BigInteger encr(BigInteger x) {
        return Utils.modPow(x, e, n);
    }

    // Encrypt message with custom key
    public static BigInteger encr(BigInteger k, BigInteger x, BigInteger modulus) {
        return Utils.modPow(x, k, modulus);
    }

    // Decrypt message
    public BigInteger decr(BigInteger y) {
        return Utils.modPow(y, d, n);
    }

}