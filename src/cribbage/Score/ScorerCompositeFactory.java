package cribbage.Score;

import cribbage.Cribbage;

/** A Factory for creating Scorer Composites */
public class ScorerCompositeFactory {
    private static ScorerCompositeFactory instance = null;

    private ScorerCompositeFactory() {
        // Empty Singleton constructor
    }

    /**
     * Singleton getInstance method
     * @return The Factory instance
     */
    public static ScorerCompositeFactory getInstance() {
        if (instance == null) instance = new ScorerCompositeFactory();
        return instance;
    }

    // Factory methods -------------------------------------------------------------------------------------------------
    /** @return A Composite for scoring during the show phase */
    private ScorerComposite getScorerCompositeShow() {
        ScorerComposite composite = new ScorerComposite();
        ScorerFactory scorerFactory = ScorerFactory.getInstance();
        composite.addScorer(scorerFactory.getMilestoneScorer());
        composite.addScorer(scorerFactory.getRunScorer());
        composite.addScorer(scorerFactory.getPairScorer());
        composite.addScorer(scorerFactory.getFlushScorer());
        composite.addScorer(scorerFactory.getJackOfStarterSuitScorer());
        return composite;
    }

    /** @return A Composite for scoring during the Play phase */
    private ScorerComposite getScorerCompositePlay() {
        ScorerComposite composite = new ScorerComposite();
        ScorerFactory scorerFactory = ScorerFactory.getInstance();
        composite.addScorer(scorerFactory.getMilestoneScorerPlay());
        composite.addScorer(scorerFactory.getRunScorerPlay());
        composite.addScorer(scorerFactory.getPairScorerPlay());
        return composite;
    }

    /** @return A Composite for scoring the current phase of the game */
    public ScorerComposite getScorerComposite() {
        Cribbage cribbage = Cribbage.getInstance();
        switch (cribbage.getGamePhase()) {
            case PLAY: return getScorerCompositePlay();
            case SHOW: return getScorerCompositeShow();
            default: return null;
        }
    }
}
