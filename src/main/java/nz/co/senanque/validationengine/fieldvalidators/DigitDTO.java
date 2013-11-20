/*******************************************************************************
 * Copyright (c)2013 Prometheus Consulting
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/

package nz.co.senanque.validationengine.fieldvalidators;

/**
 * 
 * Used to validate the number of digits. There are two components: integer digits and fractonal digits.
 * 
 * @author Roger Parkinson
 * @version $Revision: 1.5 $
 */
public class DigitDTO
{
    String integerDigits;
    String fractionalDigits;
    String message = "nz.co.senanque.validationengine.digits";
    public String integerDigits()
    {
        return integerDigits;
    }
    public void setIntegerDigits(final String integerDigits)
    {
        this.integerDigits = integerDigits;
    }
    public String fractionalDigits()
    {
        return fractionalDigits;
    }
    public void setFractionalDigits(final String fractionalDigits)
    {
        this.fractionalDigits = fractionalDigits;
    }
    public String message()
    {
        return message;
    }
    
    

}
