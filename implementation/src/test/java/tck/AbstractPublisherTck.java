package tck;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

import org.reactivestreams.Publisher;
import org.reactivestreams.tck.PublisherVerification;
import org.reactivestreams.tck.TestEnvironment;
import org.reactivestreams.tck.flow.support.TestException;

import io.smallrye.mutiny.Multi;

public abstract class AbstractPublisherTck<T> extends PublisherVerification<T> {

    public AbstractPublisherTck() {
        this(10L);
    }

    public AbstractPublisherTck(long timeout) {
        super(new TestEnvironment(timeout));
    }

    @Override
    public Publisher<T> createFailedPublisher() {
        return Multi.createFrom().failure(new TestException());
    }

    @Override
    public long maxElementsFromPublisher() {
        return 1024;
    }

    /**
     * Creates an Iterable with the specified number of elements or an infinite one if
     * elements > Integer.MAX_VALUE.
     * 
     * @param elements the number of elements to return, Integer.MAX_VALUE means an infinite sequence
     * @return the Iterable
     */
    protected Iterable<Long> iterate(long elements) {
        return iterate(elements > Integer.MAX_VALUE, elements);
    }

    protected Iterable<Long> iterate(boolean useInfinite, long elements) {
        return useInfinite ? new InfiniteRange() : new FiniteRange(elements);
    }

    /**
     * Create an array of Long values, ranging from 0L to elements - 1L.
     * 
     * @param elements the number of elements to return
     * @return the array
     */
    protected Long[] array(long elements) {
        Long[] a = new Long[(int) elements];
        for (int i = 0; i < elements; i++) {
            a[i] = (long) i;
        }
        return a;
    }

    static final class FiniteRange implements Iterable<Long> {
        final long end;

        FiniteRange(long end) {
            this.end = end;
        }

        @Override
        public Iterator<Long> iterator() {
            return new FiniteRangeIterator(end);
        }

        static final class FiniteRangeIterator implements Iterator<Long> {
            final long end;
            long count;

            FiniteRangeIterator(long end) {
                this.end = end;
            }

            @Override
            public boolean hasNext() {
                return count != end;
            }

            @Override
            public Long next() {
                long c = count;
                if (c != end) {
                    count = c + 1;
                    return c;
                }
                throw new NoSuchElementException();
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }
        }
    }

    static final class InfiniteRange implements Iterable<Long> {
        @Override
        public Iterator<Long> iterator() {
            return new InfiniteRangeIterator();
        }

        static final class InfiniteRangeIterator implements Iterator<Long> {
            long count;

            @Override
            public boolean hasNext() {
                return true;
            }

            @Override
            public Long next() {
                return count++;
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }
        }
    }

    /**
     * An infinite stream of integers starting from one.
     */
    Multi<Integer> infiniteStream() {
        return Multi.createFrom().iterable(() -> {
            AtomicInteger value = new AtomicInteger();
            return IntStream.generate(value::incrementAndGet).boxed().iterator();
        });
    }
}
