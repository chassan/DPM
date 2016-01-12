/**
* @author Sean Lawlor
* @date November 3, 2011
* @class ECSE 211 - Design Principle and Methods
*/
package bluetooth;

/**
 * Defender: value 1, string "D"
 * Attacker: value 2, string "A"
 *
 */
public enum PlayerRole {
	DEFENDER(1, "D"),
	ATTACKER(2, "A"),
	NULL(0, "");
	
	private int role;
	private String str;
	
	private PlayerRole(int rl, String str) {
		this.role = rl;
		this.str = str;
	}
	
	public String toString() {
		return this.str;
	}
	
	/**
	 * @return the value of the role
	 */
	public int getId() {
		return this.role;
	}
	
	public static PlayerRole lookupRole(int rl) {
		for (PlayerRole role : PlayerRole.values())
			if (role.getId() == rl)
				return role;
		return PlayerRole.NULL;
	}
}
