import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class ClarkeWrightAlg {
	// variables
	private ArrayList<Route> _pairs;
	private VRProblem _prob;
	private ArrayList<Route> _routeList;
	private ArrayList<ArrayList<Customer>> _soln;

	// create routes using Clarke Wright Algorithm
	public ArrayList<ArrayList<Customer>> solution(VRProblem prob) {
		this._prob = prob;
		this._soln = new ArrayList<ArrayList<Customer>>();
		createPairs();

		Route.set_depot(_prob.depot);
		for (Route r : _pairs) {
			r.calcSaving();
		}
		_pairs.sort(null);
		// buildRoutes();
		sequeltial();
		return _soln;
	}

	// creates routes for every possible pair of customers
	private void createPairs() {
		// set the depot of all routes so costs can be calculated later
		Route.set_depot(_prob.depot);
		// declare ArrayList for storage of pairs
		this._pairs = new ArrayList<Route>();
		// loop through every customer
		for (int i = 0; i < _prob.customers.size(); i++) {
			// loop through every customer
			for (int j = i; j < _prob.customers.size(); j++) {
				// if the pair is not the same customer twice
				if (i != j) {
					// create a new route
					Route route = new Route();
					// add each customer to route
					route.addToEnd(_prob.customers.get(i));
					route.addToEnd(_prob.customers.get(j));
					// add route to ArrayList
					_pairs.add(route);
				}
			}
		}
	}

	// combine pairs to create full routes
	private void buildRoutes() {
		// ArrayList for routes whilst being built
		this._routeList = new ArrayList<Route>();
		// loop through every pair of customers
		for (Route r : _pairs) {
			// get customer from pair
			Customer c1 = r.getFirstCustomer();
			Customer c2 = r.getLastCustomer();
			// create booleans for each customer
			boolean cust1 = false, cust2 = false;
			// check if either customer from pair is already route
			for (Route route : _routeList) {
				if (route.get_customerList().contains(c1)) {
					cust1 = true;
				}
				if (route.get_customerList().contains(c2)) {
					cust2 = true;
				}
			}
			// if neither customer is route
			if (cust1 == false && cust2 == false) {
				// check that pair does not go over capacity
				if (c1.c + c2.c <= _prob.depot.c) {
					// makes new route out of pair
					Route newR = new Route();
					newR.addToEnd(c1);
					newR.addToEnd(c2);
					_routeList.add(newR);
				}
				// if first customer not in route
			} else if (cust1 == false) {
				for (Route route : _routeList) {
					// check if any route ends in customer 2
					if (route.getLastCustomer() == c2) {
						// check if merging would go over capacity
						if ((route.get_requirment() + c1.c) <= _prob.depot.c) {
							// append customer to end of route
							route.addToEnd(c1);
						}
						// check if route starts with customer 2
					} else if (route.getFirstCustomer() == c2) {
						// check if merging will not go over capacity
						if ((route.get_requirment() + c1.c) <= _prob.depot.c) {
							// add customer to start
							route.addToStart(c1);
						}
					}
				}
				// if second customer is not in a route
			} else if (cust2 == false) {
				for (Route route : _routeList) {
					// check if any routes end with customer 1
					if (route.getLastCustomer() == c1) {
						// check if merging will not go over capacity
						if ((route.get_requirment() + c2.c) <= _prob.depot.c) {
							// append customer to end of route
							route.addToEnd(c2);
						}
						// check if any routes begin with customer 1
					} else if (route.getFirstCustomer() == c1) {
						// check if merging will not go over capacity
						if ((route.get_requirment() + c2.c) <= _prob.depot.c) {
							// append customer to start of route
							route.addToStart(c2);
						}
					}
				}
			}
		}
		sortRoutes();
		for (int i = 0; i < _routeList.size(); i++) {
			for (int j = 0; j < _routeList.size(); j++) {
				Route r1 = _routeList.get(i);
				Route r2 = _routeList.get(j);

				if (r1.equals(r2) != true) {
					int mergeCap = r1.get_requirment() + r2.get_requirment();
					if (mergeCap <= _prob.depot.c) {
						r1.merge(r2);
						_routeList.remove(j);
					}
				}
			}
		}
		// move routes to solution array
		for (Route r : _routeList) {
			_soln.add(r.get_customerList());
		}
	}

	private void sequeltial() {
		System.out.println(_pairs.size());
		this._routeList = new ArrayList<Route>();
		boolean empty = false;
		while (!empty) {
			for (int i = 0; i < _pairs.size(); i++) {
				if (_pairs.get(i).get_requirment() < _prob.depot.c) {
					_routeList.add(_pairs.get(i));
					_pairs.remove(i);
					empty = true;
					break;
				}
			}
		}

		for (int i = 0; i < _pairs.size(); i++) {
			// get customer from pair
			Route p = _pairs.get(i);
			if (p.get_requirment() <= _prob.depot.c) {

				Customer c1 = _pairs.get(i).getFirstCustomer();
				Customer c2 = _pairs.get(i).getLastCustomer();
				// create booleans for each customer
				boolean cust1 = false, cust2 = false;
				// check if either customer from pair is already route
				for (int j = 0; j < _routeList.size(); j++) {
					if (_routeList.get(j).get_customerList().contains(c1)) {
						cust1 = true;
					}
					if (_routeList.get(j).get_customerList().contains(c2)) {
						cust2 = true;
					}
				}
				if (!cust1) {
					for (int j = 0; j < _routeList.size(); j++) {
						// check if any route ends in customer 2
						if (_routeList.get(j).getLastCustomer() == c2) {
							// check if merging would go over capacity
							if ((_routeList.get(j).get_requirment() + c1.c) <= _prob.depot.c) {
								// append customer to end of route
								_routeList.get(j).addToEnd(c1);
								_pairs.remove(i);
								i--;
							}
						} else if (_routeList.get(j).getFirstCustomer() == c2) {
							if ((_routeList.get(j).get_requirment() + c1.c) <= _prob.depot.c) {
								// append customer to end of route
								_routeList.get(j).addToStart(c1);
								_pairs.remove(i);
								i--;
							}
						}
					}
				} else if (!cust2) {
					for (int j = 0; j < _routeList.size(); j++) {
						// check if any route ends in customer 2
						if (_routeList.get(j).getLastCustomer() == c1) {
							// check if merging would go over capacity
							if ((_routeList.get(j).get_requirment() + c2.c) <= _prob.depot.c) {
								// append customer to end of route
								_routeList.get(j).addToEnd(c2);
								_pairs.remove(i);
								i--;
							}
						} else if (_routeList.get(j).getFirstCustomer() == c1) {
							if ((_routeList.get(j).get_requirment() + c2.c) <= _prob.depot.c) {
								// append customer to end of route
								_routeList.get(j).addToStart(c2);
								_pairs.remove(i);
								i--;
							}
						}
					}
				}
			} else {
				_pairs.remove(i);
				i--;
			}
		}
		System.out.println(_pairs.size());

		for (

		Route r : _routeList)

		{
			_soln.add(r.get_customerList());
		}

	}

	private void sortRoutes() {
		Collections.sort(_routeList, new Comparator<Route>() {
			public int compare(Route r1, Route r2) {
				return r2.get_requirment() - r1.get_requirment();
			}
		});
	}

}
// end of class