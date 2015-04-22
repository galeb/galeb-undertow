package io.galeb.undertow.nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.xnio.OptionMap;
import org.xnio.XnioIoThread;
import org.xnio.XnioWorker;

public class FakeXnioWorker {

    private FakeXnioWorker() {
        // Utility classes, which are a collection of static members,
        // are not meant to be instantiated.
    }

    public static final XnioWorker NULL = new XnioWorker(null, null, OptionMap.EMPTY, null) {

        @Override
        public void shutdown() {
            // NULL
        }

        @Override
        public List<Runnable> shutdownNow() {
            return new ArrayList<Runnable>();
        }

        @Override
        public boolean isShutdown() {
            return false;
        }

        @Override
        public boolean isTerminated() {
            return false;
        }

        @Override
        public boolean awaitTermination(long timeout, TimeUnit unit) {
            return false;
        }

        @Override
        public void awaitTermination() {
            // NULL
        }

        @Override
        public int getIoThreadCount() {
            return 0;
        }

        @Override
        protected XnioIoThread chooseThread() {
            return FakeXnioIoThread.NULL;
        }
    };
}
