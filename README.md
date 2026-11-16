# CS2114Proj3
# Brew Queue

## Reflection

### How Did Our Design Absorb the Change?

For our updated feature, we added a predefined drink menu and automatic order assignment for multiple baristas. Instead of asking customers to enter a drink name and preparation time manually, the program now lets them select from a fixed menu. The model also automatically starts waiting orders whenever a barista becomes available. We also added a requeue option. 

The main classes involved were:

- **BrewQueueView:** Added the drink menu and input for selecting a drink and the requeue option.
- **BrewQueueController:** Connects the drink selection to its name and preparation time, then sends the order to the model. It also displays whether an order started immediately or had to wait.
- **BrewQueueModel:** Handles automatic assignment of waiting orders when a new order is added, an order is completed, or the number of active baristas changes.
- **BrewQueueModelTest and BrewQueueControllerTest:** These tests need to cover the new order assignment and drink selection behavior.

In our D3 Next Steps, we predicted that the skip-and-requeue feature would require changes to `BrewQueueModel`, `BrewQueueController`, and `BrewQueueView`. That prediction was reasonable for the feature we originally planned, but our priorities changed. We focused on the drink menu and multiple baristas instead. One thing that surprised us was that automatic assignment could be handled inside the model without requiring changes to the queue implementation.

### Did the Change Cross the Model/View Line?

The change involved all three main classes, but each class kept its own responsibility.

The view had to learn how to display the drink menu and collect a drink selection. However, it did not need to know how the model stores orders or assigns baristas. The controller translates the selection into a drink name and preparation time before passing the information to the model.

The automatic barista assignment was handled by the model. The controller only communicates the result to the user. This allowed us to keep the actual order-management rules out of the view.

Our `LinkedQueue` and `Order` classes did not need to change for these features. The existing queue already supported adding and removing waiting orders, and the model could use the existing order information.

### What Changed from Our D3 Plan?

Our original D3 plan focused on skip-and-requeue. We predicted adding a `requeueOrder(int orderId)` method to the model, a controller handler, and a new view menu option.

Instead, we prioritized three different features:

- A fixed drink menu with predetermined preparation times.
- Multiple baristas with automatic assignment of waiting orders.
- Requeue canceled orders 

The multiple-barista feature required additional model behavior so that available baristas could automatically receive waiting orders. This was different from our original plan because it changed how orders move between the waiting and in-progress collections.

### What Doesn't Work Yet?

Our current implementation focuses on the main ordering process and the new features. Some features from our original specification are still not included in the menu:

- Displaying overdue-order reminders.
- Changing the number of active baristas through the menu.

We also need to make sure our tests cover automatic assignment when multiple baristas are available, when all baristas are busy, and when an order is completed. Invalid drink selections, quantities, and order IDs should also be tested.

One design improvement that would make future features cheaper is to separate drink information into its own class or menu structure. This would let us add or change drinks and preparation times without modifying multiple controller methods. It would also make future features, such as displaying prices or calculating more accurate wait estimates, easier to implement.

## Next Steps

### What Is Left for the Final Build?

Our remaining work is prioritized as follows:

1. **Test the new features:** Make sure the drink menu uses the correct preparation times and that multiple baristas receive orders automatically.
2. **Improve input validation:** Check invalid drink selections, quantities, and order IDs so incorrect input does not interrupt the program.
3. **Finish and verify the MVP:** Make sure adding, completing, and displaying orders works correctly.
4. **Review the README and documentation:** Update instructions, feature descriptions, and attribution to match the final implementation.
5. **Fix any remaining test failures:** Run the complete test suite and address any issues before submitting.

### Which Stretch Goals Are We Keeping?

We are committing to the following features:

- **Predefined drink menu:** Customers select from available drinks, each with a predetermined preparation time.
- **Multiple baristas:** Orders are automatically assigned to available baristas, and waiting orders start when a barista becomes available.
- **Order cancellation:** Customers can cancel orders anytime and the next order is immediatly started
- **Skip-and-requeue:** You can requeue orders that have been canceled 
- **Estimated wait times:** Order times can be viewed live

We are postponing or dropping the following features to prioritize a stable final build:

- Overdue-order reminders.
- Additional order statistics.

### What Will Our 3-Minute Demo Show?

| Time | Demonstration |
|---|---|
| **0:00–0:30** | Introduce Brew Queue and explain that it manages waiting, in-progress, and completed orders. |
| **0:30–1:10** | Add several orders using the predefined drink menu. Show that customers select drinks instead of entering preparation times manually. |
| **1:10–1:50** | Demonstrate multiple baristas by adding enough orders to fill all available barista slots. Show that additional orders remain in the waiting queue. |
| **1:50–2:30** | Complete an in-progress order and demonstrate that the next waiting order automatically starts. |
| **2:30–3:00** | Display the waiting, in-progress, and completed orders. Briefly explain how the model handles automatic assignment without putting that logic in the view. |

### Team Responsibilities

| Team member | Remaining responsibilities |
|---|---|
| **Rudra** | Maintain `LinkedQueue<T>` and `BrewQueueController`; verify drink selection, automatic assignment messages, and controller tests. |
| **Andersson** | Maintain `Node<T>` and `Order`; review order state changes and help test order behavior. |
| **Will** | Maintain `BrewQueueModel`; verify automatic barista assignment, order completion, and model tests. |
| **Sophia** | Maintain `BrewQueueView`; verify the drink menu, input prompts, order displays, and invalid-input handling. |

## Attribution

| File | Team member | Contribution |
|---|---|---|
| `Node.java` | Andersson | Wrote |
| `Order.java` | Andersson | Wrote |
| `LinkedQueue.java` | Rudra | Wrote and tested |
| `LinkedQueueTest.java` | Rudra | Wrote and tested |
| `BrewQueueModel.java` | Will | Wrote; responsible for model changes and automatic assignment |
| `BrewQueueModelTest.java` | Will | Wrote and tested; should include tests for automatic assignment |
| `BrewQueueView.java` | Sophia | Wrote; responsible for the drink menu and user input |
| `BrewQueueController.java` | Rudra | Wrote; implemented controller changes for drink selection and multiple baristas |
| `BrewQueueControllerTest.java` | Rudra | Wrote and tested |
| `README.md` | Rudra | Wrote and revised |

### GenAI Note

Our group used Generative AI as a development aid during the project. We used it to explain Java concepts, review class responsibilities, suggest test cases, identify possible errors, and help with documentation. We also used it to brainstorm and develop stretch goals, including a predefined drink menu and automatic order assignment for multiple baristas.

AI suggestions were reviewed and adjusted to fit our project requirements and coding style. We did not assume that generated code was correct. We reviewed the suggested changes and used testing to check whether the implementation worked as intended.
