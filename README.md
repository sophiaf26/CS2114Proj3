# CS2114Proj3
# Brew Queue

## Running the Thin View

Thin view displays the menu, accepts user input, and displays waiting, in-progress, and completed orders.

### How to Run

1. Open the Brew Queue project in Eclipse.
2. Go to `BrewQueueController.java`.
3. then click **Run As → Java Application**.
4. The Brew Queue menu will appear in the console.
5. Enter a menu choice to add, start, complete, or display orders.
6. Enter `0` to exit the program.

The controller connects the view and model. The view handles user input and output, while the model manages the orders and queues. This keeps the user interface separate from the program's main logic.

## Reflection

### What Changed from Our Specification?

In our original specification we wanted a coffee shop order system using a queue to organize customer orders. As we developed the project, we expanded the order information and added a three-stage order process.

The main changes include:

- **Order:** Orders now contain a customer name, drink, quantity, order ID, reward-member status, and timestamps instead of only basic customer and drink information.
- **LinkedQueue:** We implemented a generic singly linked queue using `Node<T>`. It supports adding items to the front or back, removing items, checking the front, and clearing the queue.
- **BrewQueueModel:** The model now manages waiting, in-progress, and completed orders. It also handles order IDs, input validation, and the rules for starting and completing orders.
- **BrewQueueController:** The controller connects the model and view. It handles the menu, collects input through the view, calls model methods, and displays the results.
- **BrewQueueView:** The view handles console input and output. It displays the menu, order lists, and messages without managing the queue itself.

For the first prototype, we kept the controller focused on the basic MVP operations: adding orders, starting orders, completing orders, and displaying the three order lists. Features like cancellation, wait estimates, statistics, and changing the number of baristas were left out of the initial menu.

### Did the Model Really Stand Alone?

Yes. The `BrewQueueModel` manages the waiting queue, in-progress orders, and completed orders without requiring a view. It uses `LinkedQueue<Order>` to store waiting orders and separate collections for orders in progress and completed orders.

The model does not handle console input or output and does not depend on `BrewQueueView`. The controller is responsible for connecting the two classes.

The view only receives information from the controller and displays it. This keeps the model and view separate and allows the model to be tested without running the interface.

### What Happened to Our Unknowns?

During development, we figured out how to implement a generic queue using nodes, how to maintain the front and back references, and how to move orders through the waiting, in-progress, and completed stages.

We also worked out how the controller could communicate with the model and view without placing business logic in the view.

Some features we will hold of on for the next prototype, including requeuing and canceling orders, wait-time estimates, and additional order-management options.

### What Doesn't Work Yet?

The current prototype focuses on the basic MVP. The main menu does not yet include every feature from the specification.

The following features are not currently part of the MVP:

- Cancelling an order.
- Estimating an order's wait time.
- Displaying statistics.
- Updating the number of active baristas.
- Displaying overdue-order reminders.

We will work on these following the next prototype and then the final. 

## Next Steps

### What Is Left to Finish the MVP?

We want to make a drink menu instead of having to manually add drinks, and each drink from the menu will already have a predetermined amount of time it is required to make rather than inputting it manually 

### New Feature for Deliverable 4: Skip and Requeue

For Deliverable 4, we plan to add a skip and requeue feature. This will allow a barista to move an order currently in progress back to the end of the waiting queue.

We predict the following changes:

| Class | Predicted changes |
|---|---|
| `BrewQueueModel` | Add a method such as `requeueOrder(int orderId)` to move an in-progress order back into the waiting queue. |
| `BrewQueueController` | Add a handler such as `handleRequeueOrder()` to collect the order ID and call the model method. |
| `BrewQueueView` | Add a menu option and display success or error messages. |
| `LinkedQueue<T>` | Should not need changes because it already supports `enqueue()` and `remove()`. |
| `Order` | Should not need changes if its existing state methods support moving an order back to waiting. |
| `Node<T>` | Should not need changes because its existing links already support queue operations. |


### Stretch Goals

We plan to keep the following features as possible additions:

- Overdue-order reminders.
- Order cancellation.
- Estimated wait times.

We are dropping additional features that are not necessary for our planned implementation, such as automatic order generation. We will prioritize completing the MVP and the skip-and-requeue feature before attempting additional stretch goals.

### New Tests Needed

The skip-and-requeue feature will require tests for:

- Successfully moving an in-progress order back to the waiting queue.
- Confirming the order is removed from the in-progress collection.
- Confirming the order is placed at the back of the waiting queue.
- Attempting to requeue an order ID that does not exist.
- Attempting to requeue an order that is not currently in progress.
- Confirming that the queue and order collections remain correct after an unsuccessful attempt.

Additional tests will be added for any stretch goals that are implemented.

### Team Responsibilities

| Team member | Responsibilities |
|---|---|
| Rudra | Maintain `LinkedQueue<T>` and `BrewQueueController`; implement controller changes and test queue/controller behavior. |
| Andersson | Maintain `Node<T>` and `Order`; support any required order-state changes and test them. |
| Will | Maintain `BrewQueueModel`; implement the requeue logic and test model behavior. |
| Sophia | Maintain `BrewQueueView`; add the new menu option, input prompts, and output messages. |

## Attribution

The following table identifies the primary contributions to each class and test file.

| File | Team member | Contribution |
|---|---|---|
| `Node.java` | Andersson | Wrote |
| `Order.java` | Andersson | Wrote |
| `LinkedQueue.java` | Rudra | Wrote and tested |
| `LinkedQueueTest.java` | Rudra | Wrote and tested |
| `BrewQueueModel.java` | Will | Wrote |
| `BrewQueueModelTest.java` | Will | Wrote and tested |
| `BrewQueueView.java` | Sophia | Wrote |
| `BrewQueueController.java` | Rudra | Wrote |
| `BrewQueueControllerTest.java` | Rudra | Wrote and tested |
| `Readme` | Rudra | Wrote |

### GenAI Note

Our group used Generative AI as a development aid during the project. It helped explain Java concepts, review class responsibilities, suggest test cases, identify possible errors, and assist with documentation. We also used it to determine which stretch goals seem possible and cool things we can create with help
from GenAI such as multiple baristas.

AI-generated suggestions were reviewed and adjusted to fit our project specification and coding style. We tested it to check the code rather than assuming generated code was correct. 
