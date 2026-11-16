package brewqueue;

import student.TestCase;

/**
 * Tests the BrewQueueController class.
 *
 * @author Rudra Dutta (drudra)
 * @version 2026.11.08
 */
public class BrewQueueControllerTest extends TestCase{
    private BrewQueueModel model;
    private TestView view;
    private BrewQueueController controller;

    public void setUp(){
        model = new BrewQueueModel(1);
        view = new TestView();
        controller = new BrewQueueController(model, view);
    }

    public void testConstructorNullModel(){
        try {
            new BrewQueueController(null, view);
            fail("Expected IllegalArgumentException.");
        }
        catch (IllegalArgumentException e) {
            // Expected exception.
        }
    }

    public void testConstructorNullView(){
        try {
            new BrewQueueController(model, null);
            fail("Expected IllegalArgumentException.");
        }
        catch (IllegalArgumentException e) {
            // Expected exception.
        }
    }

    public void testHandleAddOrder(){
    view.stringInput = "Rudra";
    view.drinkChoiceInput = 4;
    view.intInput = 2;

    controller.handleAddOrder();

    assertEquals(0, model.getWaitingOrders().length);
    assertEquals(1, model.getInProgressOrders().length);
    assertEquals("Rudra",
        model.getInProgressOrders()[0].getCustomerName());
    }

    public void testHandleCompleteOrder(){
    Order order = model.addOrder("Rudra", "Latte", 1, 3.0, false);

    view.intInput = order.getOrderId();
    controller.handleCompleteOrder();

    assertEquals(1, model.getCompletedOrders().length);
    assertEquals(0, model.getInProgressOrders().length);
    }

    public void testDisplayOrders(){
    Order order = model.addOrder("Rudra", "Latte", 1, 3.0, false);

    controller.displayWaitingOrders();
    assertEquals(0, view.waitingOrders.length);

    controller.displayInProgressOrders();
    assertEquals(1, view.inProgressOrders.length);

    model.completeOrder(order.getOrderId());

    controller.displayCompletedOrders();
    assertEquals(1, view.completedOrders.length);
    }
    public void testRun(){
        view.intInputs = new int[] {99, 0};
        controller.run();
        assertTrue(view.invalidChoiceDisplayed);
        assertTrue(view.lastMessage.contains("Exiting Brew Queue"));
    }

    private class TestView extends BrewQueueView {
        private String stringInput = "Rudra";
        private String drinkInput = "Latte";
        private int intInput = 1;
        private double doubleInput = 3.0;
        private int[] intInputs;
        private int intInputIndex = 0;
        private String lastMessage = "";
        private boolean invalidChoiceDisplayed = false;
        private Order[] waitingOrders;
        private Order[] inProgressOrders;
        private Order[] completedOrders;

        public TestView(){
            super();
        }

        public void displayMenu(){
            // makre sure displays correctly
        }

        public void displayMessage(String message){
            lastMessage = message;

            if (message.contains("Invalid menu choice")) {
                invalidChoiceDisplayed = true;
            }
        }

        public String readNonBlankString(String prompt){
            if (prompt.toLowerCase().contains("drink")) {
                return drinkInput;
            }
            return stringInput;
        }

        public int Int(String prompt){
            if (intInputs != null && intInputIndex < intInputs.length) {
                return intInputs[intInputIndex++];
            }
            return intInput;
        }

        public double readDouble(String prompt){
            return doubleInput;
        }

        public void displayWaitingOrders(Order[] orders){
            waitingOrders = orders;
        }

        public void displayInProgressOrders(Order[] orders){
            inProgressOrders = orders;
        }

        public void displayCompletedOrders(Order[] orders){
            completedOrders = orders;
        }
    }
}
