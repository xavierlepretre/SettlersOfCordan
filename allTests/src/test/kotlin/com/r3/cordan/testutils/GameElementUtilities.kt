package com.r3.cordan.testutils

import com.r3.cordan.oracle.client.states.DiceRollState
import com.r3.cordan.primary.states.structure.GameBoardState
import net.corda.core.DeleteForDJVM
import net.corda.core.crypto.newSecureRandom
import net.corda.core.internal.DigitalSignatureWithCert
import net.corda.core.internal.SignedDataWithCert
import net.corda.core.internal.signWithCert
import net.corda.core.serialization.SerializedBytes
import net.corda.core.serialization.serialize
import net.corda.core.transactions.SignedTransaction
import net.corda.testing.internal.createTestSerializationEnv
import net.corda.testing.node.MockNetwork
import net.corda.testing.node.StartedMockNode
import net.corda.testing.node.internal.InternalMockNetwork

fun setupGameBoardForTestingAndReturnIds(gameState: GameBoardState, network: InternalMockNetwork, arrayOfAllPlayerNodesInOrder: List<StartedMockNode>): List<SignedTransaction> {
    val nonConflictingHextileIndexAndCoordinatesRound1 = arrayListOf(Pair(0, 5), Pair(0, 3), Pair(2, 0), Pair(2, 2))
    val nonConflictingHextileIndexAndCoordinatesRound2 = arrayListOf(Pair(12, 5), Pair(12, 3), Pair(14, 0), Pair(14, 2))

    val setupGameBoardTxs = arrayListOf<SignedTransaction>()

    for (i in 0..3) {
        setupGameBoardTxs.addAll(
                placeAPieceFromASpecificNodeAndEndTurn(i, nonConflictingHextileIndexAndCoordinatesRound1, gameState, network, arrayOfAllPlayerNodesInOrder, false)
        )
    }

    for (i in 3.downTo(0)) {
        setupGameBoardTxs.addAll(
                placeAPieceFromASpecificNodeAndEndTurn(i, nonConflictingHextileIndexAndCoordinatesRound2, gameState, network, arrayOfAllPlayerNodesInOrder, false)
        )
    }

    return setupGameBoardTxs
}


fun setupGameBoardForTesting(gameState: GameBoardState, network: InternalMockNetwork, arrayOfAllPlayerNodesInOrder: List<StartedMockNode>): GameBoardState {
    val nonConflictingHextileIndexAndCoordinatesRound1 = arrayListOf(Pair(0, 5), Pair(0, 3), Pair(2, 0), Pair(2, 2))
    val nonConflictingHextileIndexAndCoordinatesRound2 = arrayListOf(Pair(12, 5), Pair(12, 3), Pair(14, 0), Pair(14, 2))

    val setupGameBoardTxs = arrayListOf<SignedTransaction>()

    for (i in 0..3) {
        setupGameBoardTxs.addAll(
                placeAPieceFromASpecificNodeAndEndTurn(i, nonConflictingHextileIndexAndCoordinatesRound1, gameState, network, arrayOfAllPlayerNodesInOrder, false)
        )
    }

    for (i in 3.downTo(0)) {
        setupGameBoardTxs.addAll(
                placeAPieceFromASpecificNodeAndEndTurn(i, nonConflictingHextileIndexAndCoordinatesRound2, gameState, network, arrayOfAllPlayerNodesInOrder, false)
        )
    }

    return setupGameBoardTxs[setupGameBoardTxs.size-2]
            .coreTransaction
            .outRefsOfType<GameBoardState>()
            .first()
            .state
            .data
}

fun getDiceRollWithSpecifiedRollValue(int1: Int, int2: Int, gameBoardState: GameBoardState, oracle: StartedMockNode): DiceRollState {
    val oracleParty = oracle.info.legalIdentities.first()
    val oraclePartyAndCert = oracle.info.legalIdentitiesAndCerts.first()
    val byteArrayOfDataToSign = byteArrayOf(int1.toByte(), int2.toByte(), gameBoardState.turnTrackerLinearId.hashCode().toByte(), gameBoardState.linearId.hashCode().toByte())
    val signatureOfOracleSigningOverData = oracleParty.signWithCert { DigitalSignatureWithCert(oraclePartyAndCert.certificate, byteArrayOfDataToSign) }
    return DiceRollState(
            int1,
            int2,
            gameBoardState.turnTrackerLinearId,
            gameBoardState.linearId,
            gameBoardState.players,
            signatureOfOracleSigningOverData
    )
}

fun getDiceRollWithRandomRollValue(gameBoardState: GameBoardState, oracle: StartedMockNode, cannotBe7: Boolean = true): DiceRollState {
    val oracleParty = oracle.info.legalIdentities.first()
    val oraclePartyAndCert = oracle.info.legalIdentitiesAndCerts.first()
    var int1 = newSecureRandom().nextInt(6) + 1
    val int2 = newSecureRandom().nextInt(6) + 1

    if (cannotBe7) {
        while (int1 + int2 == 7) {
            int1 = newSecureRandom().nextInt(6) + 1
        }
    }

    val byteArrayOfDataToSign = byteArrayOf(int1.toByte(), int2.toByte(), gameBoardState.turnTrackerLinearId.hashCode().toByte(), gameBoardState.linearId.hashCode().toByte())
    val signatureOfOracleSigningOverData = oracleParty.signWithCert { DigitalSignatureWithCert(oraclePartyAndCert.certificate, byteArrayOfDataToSign) }
    return DiceRollState(
            int1,
            int2,
            gameBoardState.turnTrackerLinearId,
            gameBoardState.linearId,
            gameBoardState.players,
            signatureOfOracleSigningOverData
    )
}