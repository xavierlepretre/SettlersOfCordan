package com.r3.cordan.testutils

import net.corda.core.identity.CordaX500Name
import net.corda.core.node.NetworkParameters
import net.corda.testing.common.internal.testNetworkParameters
import net.corda.testing.internal.chooseIdentity
import net.corda.testing.internal.createTestSerializationEnv
import net.corda.testing.node.*
import net.corda.testing.node.internal.InternalMockNetwork
import net.corda.testing.node.internal.InternalMockNodeParameters
import net.corda.testing.node.internal.TestCordappInternal
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.AfterEach

abstract class BaseCordanTest {

    var network = InternalMockNetwork(
            initialNetworkParameters = testNetworkParameters(minimumPlatformVersion = 5),
            defaultParameters = MockNetworkParameters(
                notarySpecs = listOf(MockNetworkNotarySpec(CordaX500Name("Notary", "London", "GB")))
            ),
            cordappsForAllNodes = listOf(
                    TestCordapp.findCordapp("com.r3.cordan.primary.flows"),
                    TestCordapp.findCordapp("com.r3.cordan.primary.contracts"),
                    TestCordapp.findCordapp("com.r3.cordan.primary.states"),
                    TestCordapp.findCordapp("com.r3.cordan.oracle.client.flows"),
                    TestCordapp.findCordapp("com.r3.cordan.oracle.client.contracts"),
                    TestCordapp.findCordapp("com.r3.cordan.oracle.client.states"),
                    TestCordapp.findCordapp("com.r3.corda.lib.tokens.workflows"),
                    TestCordapp.findCordapp("com.r3.corda.lib.tokens.contracts"),
                    TestCordapp.findCordapp("com.r3.corda.lib.tokens.money")
            ).map { it as TestCordappInternal })

    var internalA = network.createNode(InternalMockNodeParameters())
    var internalB = network.createNode(InternalMockNodeParameters())
    var internalC = network.createNode(InternalMockNodeParameters())
    var internalD = network.createNode(InternalMockNodeParameters())

    var a = StartedMockNode.create(internalA)
    var b = StartedMockNode.create(internalB)
    var c = StartedMockNode.create(internalC)
    var d = StartedMockNode.create(internalD)

    // Get an identity for each of the players of the game.
    var p1 = a.info.chooseIdentity()
    var p2 = b.info.chooseIdentity()
    var p3 = c.info.chooseIdentity()
    var p4 = d.info.chooseIdentity()

    private val oracleName = CordaX500Name("Oracle", "New York", "US")
    val internalOracle = network.createNode(
            InternalMockNodeParameters(
                    legalName = oracleName,
                    additionalCordapps = listOf(
                            TestCordapp.findCordapp("com.r3.cordan.oracle.service") as TestCordappInternal
                    )
            )
    )
    val oracle = StartedMockNode.create(internalOracle)

    @BeforeEach
    fun setup() {
        network.runNetwork()
    }

    @AfterEach
    open fun tearDown() {
        network.stopNodes()
    }

}
