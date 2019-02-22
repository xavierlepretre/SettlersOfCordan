package com.contractsAndStates.contracts

import net.corda.core.contracts.CommandData
import net.corda.core.contracts.Contract
import net.corda.core.transactions.LedgerTransaction

// ***********************
// * Game State Contract *
// ***********************
class GameStateContract : Contract {
    companion object {
        // Used to identify our contract when building a transaction.
        const val ID = "com.contractsAndStates.contracts.GameStateContract"
    }

    // A transaction is valid if the verify() function of the contract of all the transaction's input and output states
    // does not throw an exception.
    override fun verify(tx: LedgerTransaction) {
        // Verification logic goes here.
    }

    // Used to indicate the transaction's intent.
    interface Commands : CommandData {
        class SetUpGameBoard : Commands
        class StartGame: Commands
        class EndGame: Commands
    }
}