import configparser
import psycopg2
from random import randint

ai_file = 'AIs'
ais = configparser.RawConfigParser()
ai_name = ''
ai_mood = 0.0
ai_sens = 1.0

db_con = psycopg2.connect(database='sytchie', user='sytchie', host='192.168.0.103')
db_cur = db_con.cursor()

debug = False


def set_ai(name, mood=0.0):
    try:
        ais.add_section(name)
    except configparser.DuplicateSectionError:
        pass
    ais.set(name, 'mood', mood)
    ais.write(open(ai_file + '.cfg', 'w'))


def import_ai(name):
    global ai_name
    global ai_mood
    ais.read(ai_file + '.cfg')
    ai_name = name
    try:
        ai_mood = ais.getfloat(name, 'mood')
    except configparser.NoSectionError:
        set_ai(name)


def get_table(current, length):
    return {
        5: {
            5: 'pronouns',
            4: 'verbs',
            3: 'noun_next',
            2: 'adjectives',
            1: 'nouns'
        }.get(current),
        4: {
            4: 'pronouns',
            3: 'verbs',
            2: 'noun_next',
            1: 'nouns'
        }.get(current),
        3: {
            3: 'pronouns',
            2: 'verbs',
            1: 'adjectives'
        }.get(current),
        2: {
            2: 'pronouns',
            1: 'adjectives'
        }.get(current)
    }.get(length, 'greetings')


def get_inf(verb):
    if verb.endswith('s'):
        return verb[:-1]
    elif verb.endswith('ed'):
        return verb[:-2]
    elif verb.endswith('ing'):
        return verb[:-3]
    return verb


def get_pos_fac_word(word, table):
    if table == 'noun_next' or table == 'pronouns':
        pos = 1
        if debug:
            print("Positivity factor of '%s' from table '%s': %d" % (word, table, pos))
        return pos
    elif table == 'verbs':
        word = get_inf(word)
    db_cur.execute("SELECT column_name FROM information_schema.columns WHERE table_name='%s';" % table)
    column_name = db_cur.fetchone()[0]
    db_cur.execute("SELECT pos_fac FROM %s WHERE %s = '%s';" % (table, column_name, word))
    pos = db_cur.fetchone()[0]
    if debug:
        print("Positivity factor of '%s' from table '%s': %d" % (word, table, pos))
    return pos


def get_pos_fac_phrase(words, length):
    if not words:
        return 1
    return get_pos_fac_word(words[0], get_table(len(words), length)) * get_pos_fac_phrase(words[1:], length)


def greet():
    if ai_mood >= 0:
        db_cur.execute("SELECT greeting FROM greetings WHERE pos_fac >= 0;")
    else:
        db_cur.execute("SELECT greeting FROM greetings WHERE pos_fac < 0;")
    greeting = db_cur.fetchall()
    print('%s: %s' % (ai_name, greeting[randint(0, len(greeting) - 1)][0]))


def respond(positivity):
    # TODO: Return an appropriate AI response
    if debug:
        print('Positivity of phrase: %d' % positivity)
    response = 'TODO'
    print('%s: %s' % (ai_name, response))
    return response


def say(message):
    global ai_mood
    words = message.lower().split()
    if words[0] == '/quit':
        ais.set(ai_name, 'mood', ai_mood)
        ais.write(open(ai_file + '.cfg', 'w'))
        exit(0)
    else:
        positivity = ai_sens * get_pos_fac_phrase(words, len(words))
        respond(positivity)
        ai_mood += positivity


def main(name):
    import_ai(name)
    greet()
    while True:
        print('\n-----------------------------------------\n')
        print('Current mood of %s: %.1f\n' % (ai_name, ai_mood))
        say(input('You: '))


main('Dude')
