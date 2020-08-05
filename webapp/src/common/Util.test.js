import {isBlank} from "./Util";
import each from "jest-each";

describe('TEST SUITE Util: ', () => {
    describe('isBlank - ', () => {
        each([undefined, null, "", "      "]).test('returns true when string is blank', (blankValue) => {
            expect(isBlank(blankValue)).toEqual(true);
        });

        each(["ABC", " a ", "."]).test('returns false when string is not blank', (nonBlankValue) => {
            expect(isBlank(nonBlankValue)).toEqual(false);
        });
    });
});