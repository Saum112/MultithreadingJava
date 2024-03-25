package interview;

public class BoundedBuffer {

	public static void main(String[] args) throws InterruptedException {

		BlockingQueue<Integer> bQueue = new BlockingQueue(14);

		Thread t1 = new Thread(new Runnable() {

			@Override
			public void run() {

				for (int i = 0; i < 50; i++) {

					try {
						bQueue.enqueue(i);
						System.out.println("enqueued element " + i);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}

			}
		});

		Thread t2 = new Thread(new Runnable() {

			@Override
			public void run() {

				try {
					for (int i = 0; i < 25; i++)
						System.out.println("Thread 2 dequeued...." + bQueue.dequeue());
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}

		});

		Thread t3 = new Thread(new Runnable() {

			@Override
			public void run() {

				try {
					for (int i = 0; i < 25; i++)
						System.out.println("Thread 3 dequeued...." + bQueue.dequeue());
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}

		});

		t1.start();
		t1.sleep(3000);
		t2.start();
		t2.join();
		t3.start();
		t1.join();
		t3.join();
	}

}

class BlockingQueue<T> {

	int size = 0;
	T[] arr;
	int capacity, head = 0, tail = 0;
	Object lock = new Object();

	@SuppressWarnings("unchecked")
	public BlockingQueue(int capacity) {

		this.capacity = capacity;
		arr = (T[]) new Object[capacity];
	}

	public void enqueue(T ele) throws InterruptedException {

		synchronized (lock) {

			while (size == capacity) {

				lock.wait();
			}

			if (tail == capacity) {

				tail = 0;
			}

			arr[tail] = ele;
			tail++;
			size++;

			lock.notifyAll();

		}

	}

	public T dequeue() throws InterruptedException {

		T ele = null;

		synchronized (lock) {

			while (size == 0) {

				lock.wait();
			}

			if (head == capacity) {

				head = 0;
			}

			ele = arr[head];
			size--;
			head++;

			lock.notifyAll();

		}

		return ele;

	}
}
