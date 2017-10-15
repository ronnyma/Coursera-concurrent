package edu.coursera.concurrent;

import edu.rice.pcdp.Actor;
import edu.rice.pcdp.PCDP;

/**
 * An actor-based implementation of the Sieve of Eratosthenes.
 * <p>
 * TODO Fill in the empty SieveActorActor actor class below and use it from
 * countPrimes to determin the number of primes <= limit.
 */
public final class SieveActor extends Sieve {
    /**
     * {@inheritDoc}
     * <p>
     * TODO Use the SieveActorActor class to calculate the number of primes <=
     * limit in parallel. You might consider how you can model the Sieve of
     * Eratosthenes as a pipeline of actors, each corresponding to a single
     * prime number.
     */

    public static int numLocalPrimes = 0;

    public SieveActor() {
        numLocalPrimes = 0;
    }

    @Override
    public int countPrimes(final int limit) {
        //multiple threads will enter this critical section
        PCDP.finish(() -> {
            final SieveActorActor actor = new SieveActorActor(2);

            for (int i = 3; i <= limit; i += 2) //just send odd numbers, since multiples of 2 are aready filtered
                actor.send(i);

            actor.send(0);
        });

        return numLocalPrimes;
    }

    /**
     * An actor class that helps implement the Sieve of Eratosthenes in
     * parallel.
     */
    public static final class SieveActorActor extends Actor {

        private SieveActorActor nextActor;
        private final int localPrimeNumber;


        public SieveActorActor getNextActor() {
            return nextActor;
        }

        public SieveActorActor(int prime) {
            localPrimeNumber = prime;
            numLocalPrimes++;
        }


        /**
         * Process a single message sent to this actor.
         * <p>
         * TODO complete this method.
         *
         * @param msg Received message
         */
        @Override
        public void process(final Object msg) {
            final int candidate = (Integer) msg;

            if (candidate == 0) {
                if (nextActor != null)
                    nextActor.send(0);
                return;
            }
            boolean nonMul = ((candidate % localPrimeNumber) != 0);

            if (nonMul) {
                if (nextActor == null)
                    nextActor = new SieveActorActor(candidate);
                else
                    nextActor.send(candidate);
            }
        }
    }
}
