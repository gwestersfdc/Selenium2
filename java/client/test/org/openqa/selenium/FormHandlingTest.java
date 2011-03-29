/*
Copyright 2007-2009 WebDriver committers
Copyright 2007-2009 Google Inc.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

     http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/

package org.openqa.selenium;

import static org.openqa.selenium.Ignore.Driver.ANDROID;
import static org.openqa.selenium.Ignore.Driver.CHROME;
import static org.openqa.selenium.Ignore.Driver.HTMLUNIT;
import static org.openqa.selenium.Ignore.Driver.IE;
import static org.openqa.selenium.Ignore.Driver.IPHONE;
import static org.openqa.selenium.Ignore.Driver.SELENESE;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;
import static org.openqa.selenium.TestWaiter.waitFor;
import static org.openqa.selenium.WaitingConditions.pageTitleToBe;

import java.io.File;
import java.io.IOException;

public class FormHandlingTest extends AbstractDriverTestCase {
  public void testShouldClickOnSubmitInputElements() {
    driver.get(pages.formPage);
    driver.findElement(By.id("submitButton")).click();
    waitFor(pageTitleToBe(driver, "We Arrive Here"));
    assertThat(driver.getTitle(), equalTo("We Arrive Here"));
  }

  public void testClickingOnUnclickableElementsDoesNothing() {
    driver.get(pages.formPage);
    try {
      driver.findElement(By.xpath("//body")).click();
    } catch (Exception e) {
      e.printStackTrace();
      fail("Clicking on the unclickable should be a no-op");
    }
  }

  public void testShouldBeAbleToClickImageButtons() {
    driver.get(pages.formPage);
    driver.findElement(By.id("imageButton")).click();
    waitFor(pageTitleToBe(driver, "We Arrive Here"));
    assertThat(driver.getTitle(), equalTo("We Arrive Here"));
  }

  public void testShouldBeAbleToSubmitForms() {
    driver.get(pages.formPage);
    driver.findElement(By.name("login")).submit();
    waitFor(pageTitleToBe(driver, "We Arrive Here"));
  }

  public void testShouldSubmitAFormWhenAnyInputElementWithinThatFormIsSubmitted() {
    driver.get(pages.formPage);
    driver.findElement(By.id("checky")).submit();
    waitFor(pageTitleToBe(driver, "We Arrive Here"));
  }

  public void testShouldSubmitAFormWhenAnyElementWihinThatFormIsSubmitted() {
    driver.get(pages.formPage);
    driver.findElement(By.xpath("//form/p")).submit();
    waitFor(pageTitleToBe(driver, "We Arrive Here"));
  }

  public void testShouldNotBeAbleToSubmitAFormThatDoesNotExist() {
    driver.get(pages.formPage);

    try {
      driver.findElement(By.name("there is no spoon"))
          .submit();
      fail("Should not have succeeded");
    } catch (NoSuchElementException e) {
      // this is expected
    }
  }

  @Ignore(value = CHROME, reason = "Issue 440")
  public void testShouldBeAbleToEnterTextIntoATextAreaBySettingItsValue() {
    driver.get(pages.javascriptPage);
    WebElement textarea = driver.findElement(By
        .id("keyUpArea"));
    String cheesey = "Brie and cheddar";
    textarea.sendKeys(cheesey);
    assertThat(textarea.getValue(), equalTo(cheesey));
  }

  @Ignore(value = {SELENESE, CHROME, IPHONE, IE},
      reason = "IE: New failure for IE \n." +
        "Chrome: Issue 440")
  public void testShouldSubmitAFormUsingTheNewlineLiteral() {
    driver.get(pages.formPage);
    WebElement nestedForm = driver.findElement(By.id("nested_form"));
    WebElement input = nestedForm.findElement(By.name("x"));
    input.sendKeys("\n");
    assertEquals("We Arrive Here", driver.getTitle());
    assertTrue(driver.getCurrentUrl().endsWith("?x=name"));
  }

  @Ignore({SELENESE, CHROME, IPHONE, IE})
  public void testShouldSubmitAFormUsingTheEnterKey() {
    driver.get(pages.formPage);
    WebElement nestedForm = driver.findElement(By.id("nested_form"));
    WebElement input = nestedForm.findElement(By.name("x"));
    input.sendKeys(Keys.ENTER);
    waitFor(pageTitleToBe(driver, "We Arrive Here"));
    assertTrue(driver.getCurrentUrl().endsWith("?x=name"));
  }

  @Ignore(value = CHROME, reason="Issue 440")
  public void testShouldEnterDataIntoFormFields() {
    driver.get(pages.xhtmlTestPage);
    WebElement element = driver.findElement(By
        .xpath("//form[@name='someForm']/input[@id='username']"));
    String originalValue = element.getValue();
    assertThat(originalValue, equalTo("change"));

    element.clear();
    element.sendKeys("some text");

    element = driver.findElement(By
        .xpath("//form[@name='someForm']/input[@id='username']"));
    String newFormValue = element.getValue();
    assertThat(newFormValue, equalTo("some text"));
  }

  @Ignore(value = {CHROME, SELENESE, IPHONE, ANDROID, IE},
      reason = "Does not yet support file uploads")
  public void testShouldBeAbleToAlterTheContentsOfAFileUploadInputElement() throws IOException {
    driver.get(pages.formPage);
    WebElement uploadElement = driver.findElement(By.id("upload"));
    assertThat(uploadElement.getValue(), equalTo(""));

    File file = File.createTempFile("test", "txt");
    file.deleteOnExit();

    uploadElement.sendKeys(file.getAbsolutePath());

    File value = new File(uploadElement.getValue());
    assertThat(value.getCanonicalPath(), equalTo(file.getCanonicalPath()));
  }

  @Ignore(value = {CHROME, SELENESE, IPHONE, ANDROID},
      reason = "Does not yet support file uploads")
  public void testShouldBeAbleToUploadTheSameFileTwice() throws IOException {
    File file = File.createTempFile("test", "txt");
    file.deleteOnExit();

    driver.get(pages.formPage);
    WebElement uploadElement = driver.findElement(By.id("upload"));
    assertThat(uploadElement.getValue(), equalTo(""));

    uploadElement.sendKeys(file.getAbsolutePath());
    uploadElement.submit();

    driver.get(pages.formPage);
    uploadElement = driver.findElement(By.id("upload"));
    assertThat(uploadElement.getValue(), equalTo(""));

    uploadElement.sendKeys(file.getAbsolutePath());
    uploadElement.submit();

    // If we get this far, then we're all good.
  }

  @Ignore(value = {IPHONE, CHROME}, reason = "iPhone: sendKeys implemented incorrectly.\n" +
      "Chrome: Issue 440")
  public void testSendingKeyboardEventsShouldAppendTextInInputs() {
    driver.get(pages.formPage);
    WebElement element = driver.findElement(By.id("working"));
    element.sendKeys("Some");
    String value = element.getValue();
    assertThat(value, is("Some"));

    element.sendKeys(" text");
    value = element.getValue();
    assertThat(value, is("Some text"));
  }

  @Ignore(value = {IE, HTMLUNIT, CHROME, SELENESE, IPHONE},
          reason = "Not implemented going to the end of the line first;\n"
                   + "  iPhone: sendKeys not implemented correctly")
  public void testSendingKeyboardEventsShouldAppendTextinTextAreas() {
    driver.get(pages.formPage);
    WebElement element = driver.findElement(By.id("withText"));

    element.sendKeys(". Some text");
    String value = element.getValue();

    assertThat(value, is("Example text. Some text"));
  }

  @Ignore(value = CHROME, reason = "Issue 440")
  public void testShouldBeAbleToClearTextFromInputElements() {
    driver.get(pages.formPage);
    WebElement element = driver.findElement(By.id("working"));
    element.sendKeys("Some text");
    String value = element.getValue();
    assertThat(value.length(), is(greaterThan(0)));

    element.clear();
    value = element.getValue();

    assertThat(value.length(), is(0));
  }

  public void testEmptyTextBoxesShouldReturnAnEmptyStringNotNull() {
    driver.get(pages.formPage);
    WebElement emptyTextBox = driver.findElement(By.id("working"));
    assertEquals(emptyTextBox.getValue(), "");
  }

  public void testShouldBeAbleToClearTextFromTextAreas() {
    driver.get(pages.formPage);
    WebElement element = driver.findElement(By.id("withText"));
    element.sendKeys("Some text");
    String value = element.getValue();
    assertThat(value.length(), is(greaterThan(0)));

    element.clear();
    value = element.getValue();

    assertThat(value.length(), is(0));
  }
}
