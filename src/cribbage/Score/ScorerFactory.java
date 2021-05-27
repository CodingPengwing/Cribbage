package cribbage.Score;

/** A Factory for creating all the different Scorer Strategies */
public class ScorerFactory {
    private static ScorerFactory instance = null;

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
    public static ScorerFactory getInstance() {
        if (instance == null) {
            instance = new ScorerFactory();
        }
        return instance;
    }

    // Factory methods -------------------------------------------------------------------------------------------------
    /** @return A new FlushScorer instance */
    public FlushScorer getFlushScorer() {
        return flushScorer;
    }

    /** @return A new GoScorer instance */
    public GoScorer getGoScorer() {
        return goScorer;
    }

    /** @return A new JackOfStarterSuitScorer instance */
    public JackOfStarterSuitScorer getJackOfStarterSuitScorer() {
        return jackOfStarterSuitScorer;
    }

    /** @return A new JackStarterScorer instance */
    public StarterScorer getStarterScorer() {
        return starterScorer;
    }

    /** @return A new MilestoneScorer instance */
    public MilestoneScorer getMilestoneScorer() {
        return milestoneScorer;
    }

    /** @return A new MilestoneScorerPlay instance */
    public MilestoneScorerPlay getMilestoneScorerPlay() {
        return milestoneScorerPlay;
    }

    /** @return A new PairScorer instance */
    public PairScorer getPairScorer() {
        return pairScorer;
    }

    /** @return A new PairScorerPlay instance */
    public PairScorerPlay getPairScorerPlay() {
        return pairScorerPlay;
    }

    /** @return A new RunScorer instance */
    public RunScorer getRunScorer() {
        return runScorer;
    }

    /** @return A new RunScorerPlay instance */
    public RunScorerPlay getRunScorerPlay() {
        return runScorerPlay;
    }
}
