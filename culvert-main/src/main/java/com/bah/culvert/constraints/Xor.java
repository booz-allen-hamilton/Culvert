/**
 * Copyright 2011 Booz Allen Hamilton.
 * 
 * See the NOTICE file distributed with this work for additional
 * information regarding copyright ownership. Booz Allen Hamilton
 * licenses this file to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */ 
package com.bah.culvert.constraints;

import java.util.Arrays;
import java.util.List;

import com.bah.culvert.data.Result;
import com.bah.culvert.iterators.SeekingCurrentIterator;
import com.bah.culvert.util.Bytes;

public final class Xor extends Logic {

  /**
   * For use with {@link #readFields(java.io.DataInput)}
   */
  public Xor() {

  }

  public Xor(Constraint... subConstraints) {
	this(Arrays.asList(subConstraints));
  }

  public Xor(List<Constraint> asList) {
	super(asList);
  }

  @Override
  public String toString() {
	return new StringBuilder().append("Xor(").append(super.toString())
		.append(")").toString();
  }

  @Override
  public boolean equals(Object a) {
	if (!(a instanceof Xor)) {
	  return false;
	}
	return super.equals(a);
  }

  @Override
  public int hashCode() {
	int code = super.hashCode();
	return code + 2;
  }

  /**
   * Get the next result that represents the union of the underlying constraints
   * (based on row id)
   * @param resultIterators
   * @return
   */
  @Override
  protected Result nextResult(SeekingCurrentIterator[] resultIterators) {
	// assemble a list of candidate keys for the next key
	Result lowestResult = null;
	byte[] lowestBytes = null;
	SeekingCurrentIterator lowestIterator = null;
	boolean duplicateFound = false;
	int[] duplicates = new int[resultIterators.length];
	int lastLowestIndex = 0;
	while(duplicateFound){
		lowestResult = null;
		lowestBytes = null;
		lowestIterator = null;
		duplicateFound = false;
		duplicates = new int[resultInterators.length];
		lastLowestIndex = 0;
		
		for (int i = 0; i < resultIterators.length; i++) {
		  if (!resultIterators[i].isMarkedDoneWith()) {
			Result current = resultIterators[i].current();
			// this is called on the first use of the iterator
			if (current == null) {
			  if (resultIterators[i].hasNext())
				current = resultIterators[i].next();
			  else {
				resultIterators[i].markDoneWith();
				continue;
			  }
			}

			// and this is used when an iterator is exhausted
			assert (current != null);

			byte[] key = current.getRecordId();
			
			duplicates[i] = 0;
			if (lowestResult == null || Bytes.compareTo(key, lowestBytes) < 0) {
			  lowestResult = current;
			  lowestBytes = key;
			  lowestIterator = resultIterators[i];
			  
			  lastLowestIndex = i;
			  duplicateFound = false;
			  for(int i = 0; i < duplicates.length; i++)
				duplicates[i] = 0;
				
			} else if (Bytes.compareTo(key, lowestBytes) == 0) {
			  // in this case, on XOR, we've found a duplicate, so mark this to pass
			  duplicateFound = true;
			  duplicates[i] = 1;
			  duplicates[lastLowestIndex] = 1;
			}
		  }
		}
		
		if(duplicateFound){
			for(int j = 0; j < duplicates.length; j++){
				if(duplicates[j] > 0){
				  if (resultIterators[j].hasNext())
					resultIterators[j].next();
				  else
					resultIterators[j].markDoneWith();
				}
			}
			continue;
		}
				
		if (lowestIterator == null) {
		  return null;
		}
	}

	if (lowestIterator.hasNext()) {
	  lowestIterator.next();
	} else {
	  lowestIterator.markDoneWith();
	}

	return lowestResult;
  }

}
