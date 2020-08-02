// from: https://ranjithnair.github.io/2018/02/27/Reddit-Application-Oauth.html
const express = require('express')
const {URLSearchParams} = require('url')
// var router = express.Router()
const port = 3001;
const app = express();

const fetch = require('node-fetch')

const REDDIT_ACCESS_TOKEN_URL = 'https://www.reddit.com/api/v1/access_token'
const APP_ONLY_GRANT_TYPE = 'https://oauth.reddit.com/grants/installed_client'

const REDDIT_CLIENT_ID = 'Cz3RfZHd1xgl2g';

const fetchRedditTrendingData = (sub, accessToken) =>
    fetch(`https://oauth.reddit.com/r/${sub}/top`, {
        headers: {
            Authorization: `Bearer ${accessToken}`
        }
    }).then(data => data.json())

app.get('/test', async function (req, res, next) {
    // Getting the reddit sub from the POST body request
    const redditSub = 'earthporn'

    try {
        // Creating Body for the POST request which are URL encoded
        const params = new URLSearchParams()
        params.append('grant_type', APP_ONLY_GRANT_TYPE)
        params.append('device_id', 'DO_NOT_TRACK_THIS_DEVICE')

        // Put password as empty
        const authorization = Buffer.from(`${REDDIT_CLIENT_ID}:`).toString('base64');

        console.log('authorization', authorization)

        // Trigger POST to get the access token
        const tokenData = await fetch(REDDIT_ACCESS_TOKEN_URL, {
            method: 'POST',
            body: params,
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded',
                'Authorization': `Basic ${authorization}`,
            }
        }).then(res => res.json())

        if (!tokenData.error) {
            // Fetch Reddit data by passing in the access_token
            const trendData = await fetchRedditTrendingData(redditSub, tokenData.access_token)

            // Finding just the title of the post
            const trendingResult = trendData.data.children.map(
                child => child.data.title
            )

            res.send(trendingResult)
        }
        // Handling OAuth error
        res.status(tokenData.error).send(tokenData.message)
    } catch (error) {
        console.log(error)
        res.status(500).send('Unexpected Error !')
    }
})

app.get('/', (req, res) => {
    res.send('Hello World!')
})

app.listen(port, () => {
    console.log(`Example app listening at http://localhost:${port}`)
})
