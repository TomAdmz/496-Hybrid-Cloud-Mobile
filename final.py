from google.appengine.ext import ndb
import webapp2
import json

class Workout(ndb.Model):
    id = ndb.StringProperty()
    userId = ndb.StringProperty(required=True)
    warmup = ndb.StringProperty()
    first = ndb.StringProperty()
    second = ndb.StringProperty()
    third = ndb.StringProperty()
    cooldown = ndb.StringProperty()

class WorkoutHandler(webapp2.RequestHandler):
    def post(self):
        workout_data = json.loads(self.request.body)
        new_workout = Workout(userId=workout_data['userId'])
        if "warmup" in workout_data:
            new_workout.warmup = workout_data['warmup']
        else:
            new_workout.warmup = ''
        if "first" in workout_data:
            new_workout.first = workout_data['first']
        else:
            new_workout.first = ''
        if "second" in workout_data:
            new_workout.second = workout_data['second']
        else:
            new_workout.second = ''
        if "third" in workout_data:
            new_workout.third = workout_data['third']
        else:
            new_workout.third = ''
        if "cooldown" in workout_data:
            new_workout.cooldown = workout_data['cooldown']
        else:
            new_workout.cooldown = ''
        workout_key = new_workout.put()

        new_workout.id = workout_key.urlsafe()
        new_workout.put()

        workout_dict = new_workout.to_dict()
        workout_dict['self'] = '/workouts/' + new_workout.key.urlsafe()

        self.response.write(json.dumps(workout_dict))
        self.response.status_int = 201

    def get(self, id=None):
        #get specific Workout
        if id:
            b = ndb.Key(urlsafe=id).get()
            b_d = b.to_dict()
            b_d['self'] = "/workouts/" + id
            self.response.write(json.dumps(b_d))
        #get workouts by userId query
        elif self.request.get('userId'):
            if self.request.get('userId'):
                userId = self.request.get('userId')
            b = ndb.get_multi(
            Workout.query(Workout.userId == userId).fetch(keys_only=True)
            )
            workout_list = []
            for workout in b:
                b_d = workout.to_dict()
                b_d['self'] = "/workouts/" + b_d['id']
                workout_list.append(b_d)
            self.response.write(json.dumps(workout_list))
        #get all workouts
        else:
            b = ndb.get_multi(
            Workout.query().fetch(keys_only=True)
            )
            workout_list = []
            for workout in b:
                b_d = workout.to_dict()
                b_d['self'] = "/workouts/" + b_d['id']
                workout_list.append(b_d)
            self.response.write(json.dumps(workout_list))

    def delete(self, id=None):
        if id:
            b = ndb.Key(urlsafe=id).get()
            b_d = b.to_dict()
            b_d['self'] = "/workouts/" + id

            b.key.delete()

    def put(self, id=None):
        if id:
            b = ndb.Key(urlsafe=id).get()
            workout_data = json.loads(self.request.body)

            if "userId" in workout_data:
                b.userId = workout_data['userId']
            else:
                b.title = ''
            if "warmup" in workout_data:
                b.warmup = workout_data['warmup']
            else:
                b.warmup = ''
            if "first" in workout_data:
                b.first = workout_data['first']
            else:
                b.first = ''
            if "second" in workout_data:
                b.second = workout_data['second']
            else:
                b.second = ''
            if "third" in workout_data:
                b.third = workout_data['third']
            else:
                b.third = ''
           	if "cooldown" in workout_data:
           		b.cooldown = workout_data['cooldown']
           	else:
           		b.cooldown = ''
            b.put()

    def patch(self, id=None):
        if id:
            b = ndb.Key(urlsafe=id).get()
            workout_data = json.loads(self.request.body)

            for data in workout_data:
                if data == "cooldown":
                    b.cooldown = workout_data[data]
                if data == "warmup":
                    b.warmup = workout_data[data]
                if data == "first":
                    b.first = workout_data[data]
                if data == "second":
                    b.second = workout_data[data]
                if data == "third":
                    b.third = workout_data[data]
            b.put()


# [START main_page]
class MainPage(webapp2.RequestHandler):

    def get(self):
        self.response.write("hello")

    #delete entire DB
    def delete(self):
        ndb.delete_multi(
        Workout.query().fetch(keys_only=True)
        )


allowed_methods = webapp2.WSGIApplication.allowed_methods
new_allowed_methods = allowed_methods.union(('PATCH',))
webapp2.WSGIApplication.allowed_methods = new_allowed_methods
app = webapp2.WSGIApplication([
    ('/', MainPage),
    ('/workouts', WorkoutHandler),
    ('/workouts/(.*)', WorkoutHandler),
], debug=True)
