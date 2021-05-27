package cribbage;

/** A Factory for creating all the different Scorer Strategies */
public class ScorerFactory {
    private static ScorerFactory instance = null;

    private ScorerFactory() {
        // Empty singleton constructor
    }

    /**
     * Singleton getInstance method
     * @return The Factory instance
     */
    public static ScorerFactory getInstance() {
        if (instance == null) {
            instance = new ScorerFactory();
        }
        return instance;
    }

    // Factory methods -------------------------------------------------------------------------------------------------
    /** @return A new FlushScorer instance */
    public FlushScorer getFlushScorer() {
        return new FlushScorer();
    }

    /** @return A new GoScorer instance */
    public GoScorer getGoScorer() {
        return new GoScorer();
    }

    /** @return A new JackOfStarterSuitScorer instance */
    public JackOfStarterSuitScorer getJackOfStarterSuitScorer() {
        return new JackOfStarterSuitScorer();
    }

    /** @return A new JackStarterScorer instance */
    public JackStarterScorer getJackStarterScorer() {
        return new JackStarterScorer();
    }

    /** @return A new MilestoneScorer instance */
    public MilestoneScorer getMilestoneScorer() {
        return new MilestoneScorer();
    }

    /** @return A new MilestoneScorerPlay instance */
    public MilestoneScorerPlay getMilestoneScorerPlay() {
        return new MilestoneScorerPlay();
    }

    /** @return A new PairScorer instance */
    public PairScorer getPairScorer() {
        return new PairScorer();
    }

    /** @return A new PairScorerPlay instance */
    public PairScorerPlay getPairScorerPlay() {
        return new PairScorerPlay();
    }

    /** @return A new RunScorer instance */
    public RunScorer getRunScorer() {
        return new RunScorer();
    }

    /** @return A new RunScorerPlay instance */
    public RunScorerPlay getRunScorerPlay() {
        return new RunScorerPlay();
    }
}
