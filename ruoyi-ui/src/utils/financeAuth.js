import Cookies from 'js-cookie'

const TokenKey = 'Finance-Token'

export function getFinanceToken() {
  return Cookies.get(TokenKey)
}

export function setFinanceToken(token) {
  return Cookies.set(TokenKey, token)
}

export function removeFinanceToken() {
  return Cookies.remove(TokenKey)
}