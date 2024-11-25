package by.server.RSA;

import java.math.BigInteger;
import java.util.Random;

import static java.math.BigInteger.*;

public class Utils {

    public static BigInteger[] extendedEuclid(BigInteger a, BigInteger b) {
        BigInteger oldR = a;
        BigInteger r = b;
        //Bezout coefficients
        BigInteger oldS = ONE;
        BigInteger s = ZERO;
        BigInteger oldT = ZERO;
        BigInteger t = ONE;

        BigInteger q;
        while (!r.equals(ZERO)) {
            q = oldR.divide(r);
            BigInteger temp;

            temp = r;
            r = oldR.subtract(q.multiply(temp));
            oldR = temp;

            temp = s;
            s = oldS.subtract(q.multiply(temp));
            oldS = temp;

            temp = t;
            t = oldT.subtract(q.multiply(temp));
            oldT = temp;
        }

        //a * oldS + b * oldT = oldR
        return new BigInteger[]{oldR, oldS.mod(b), oldT.mod(a)};
    }

    public static BigInteger modPow(BigInteger a, BigInteger b, BigInteger n) {
        BigInteger u = ONE;
        BigInteger v = a;

        int bitLength = b.bitLength();

        for (int i = 0; i < bitLength; ++i) {
            if (b.testBit(i)) {
                u = u.multiply(v).mod(n);
            }
            v = v.multiply(v).mod(n);
        }
        return u;
    }

    public static BigInteger genPrime(int len) {
        Random random = new Random();

        BigInteger a = ZERO.setBit(len - 1);
        BigInteger b = ZERO.setBit(len);
        BigInteger window = b.subtract(ONE).subtract(a.add(ONE));
        BigInteger n = a.add(new BigInteger(len, random).mod(window));
        while (!isPrime(n, 100)) {
            n = a.add(new BigInteger(len, random).mod(window));
            n = n.testBit(0) ? n : n.add(ONE);
        }
        return n;
    }

    //Solovay-Strassen test with probability 1 - 2 ^ (-k)
    public static boolean isPrime(BigInteger n, int k) {
        if (n.equals(ONE)) return false;
        if (n.equals(TWO)) return true;
        if (!n.testBit(0)) return false;

        Random random = new Random();
        for (int i = 0; i < k; ++i) {
            BigInteger a;
            do {
                a = new BigInteger(n.bitLength(), random);
            } while (a.compareTo(n) > 0 || a.equals(ZERO));

            BigInteger[] gcd = extendedEuclid(a, n);
            if (gcd[0].compareTo(ONE) > 0) return false;

            BigInteger test = modPow(a, n.subtract(ONE).divide(TWO), n);
            BigInteger jacobi = jacobi(a, n).mod(n);
            if (!test.equals(jacobi)) return false;
        }
        return true;
    }

    public static BigInteger jacobi(BigInteger a, BigInteger n) {
        a = a.mod(n);
        BigInteger EIGHT = new BigInteger("8");
        BigInteger THREE = new BigInteger("3");
        BigInteger FOUR = new BigInteger("4");
        BigInteger FIVE = new BigInteger("5");
        BigInteger jacobi = ONE;
        while (a.compareTo(ZERO) > 0) {
            while (!a.testBit(0)) {
                a = a.divide(TWO);
                BigInteger r = n.mod(EIGHT);
                if (r.equals(THREE) || r.equals(FIVE)) {
                    jacobi = jacobi.multiply(new BigInteger("-1"));

                }
            }
            BigInteger temp = n;
            n = a;
            a = temp;
            if (a.mod(FOUR).equals(THREE) && n.mod(FOUR).equals(THREE)) {
                jacobi = jacobi.multiply(new BigInteger("-1"));
            }
            a = a.mod(n);
        }
        if (n.equals(ONE)) {
            return jacobi;
        }

        return ZERO;
    }
}
