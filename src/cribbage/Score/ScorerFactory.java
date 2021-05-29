package cribbage.Score;

/**
 * Factory pattern. This class creates and gives internal access to concrete Scorers.
 */
class ScorerFactory {
    private static ScorerFactory instance = null;

    // Create and hold instances of each scorer in the factory.
    // When get method is called, the same instance is returned every time.
    private static final FlushScorer flushScorer = new FlushScorer();
    private static final GoScorer goScorer = new GoScorer();
    private static final JackOfStarterSuitScorer jackOfStarterSuitScorer = new JackOfStarterSuitScorer();
    private static final StarterScorer starterScorer = new StarterScorer();
    private static final MilestoneScorer milestoneScorer = new MilestoneScorer();
    private static final MilestoneScorerPlay milestoneScorerPlay = new MilestoneScorerPlay();
    private static final PairScorer pairScorer = new PairScorer();
    private static final PairScorerPlay pairScorerPlay = new PairScorerPlay();
    private static final RunScorer runScorer = new RunScorer();
    private static final RunScorerPlay runScorerPlay = new RunScorerPlay();

    // Empty singleton constructor
    private ScorerFactory() {}

    /**
     * Singleton getInstance method
     * @return The Factory instance
     */
    static ScorerFactory getInstance() {
        if (instance == null) {
            instance = new ScorerFactory();
        }
        return instance;
    }

    // Factory methods -----------------------------------------------------------------------

    /** @return A new FlushScorer instance */
    Scorer getFlushScorer() {
        return flushScorer;
    }

    /** @return A new GoScorer instance */
    Scorer getGoScorer() {
        return goScorer;
    }

    /** @return A new JackOfStarterSuitScorer instance */
    Scorer getJackOfStarterSuitScorer() {
        return jackOfStarterSuitScorer;
    }

    /** @return A new JackStarterScorer instance */
    Scorer getStarterScorer() {
        return starterScorer;
    }

    /** @return A new MilestoneScorer instance */
    Scorer getMilestoneScorer() {
        return milestoneScorer;
    }

    /** @return A new MilestoneScorerPlay instance */
    Scorer getMilestoneScorerPlay() {
        return milestoneScorerPlay;
    }

    /** @return A new PairScorer instance */
    Scorer getPairScorer() {
        return pairScorer;
    }

    /** @return A new PairScorerPlay instance */
    Scorer getPairScorerPlay() {
        return pairScorerPlay;
    }

    /** @return A new RunScorer instance */
    Scorer getRunScorer() {
        return runScorer;
    }

    /** @return A new RunScorerPlay instance */
    Scorer getRunScorerPlay() {
        return runScorerPlay;
    }
}
