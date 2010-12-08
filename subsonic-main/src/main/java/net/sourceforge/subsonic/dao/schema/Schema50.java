/*
 This file is part of Subsonic.

 Subsonic is free software: you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.

 Subsonic is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with Subsonic.  If not, see <http://www.gnu.org/licenses/>.

 Copyright 2009 (C) Sindre Mehus
 */
package net.sourceforge.subsonic.dao.schema;

import org.springframework.jdbc.core.JdbcTemplate;

import net.sourceforge.subsonic.Logger;

import java.util.Arrays;

/**
 * Used for creating and evolving the database schema.
 * This class implements the database schema for Subsonic version 5.0.
 *
 * @author Sindre Mehus
 */
public class Schema50 extends Schema {

    private static final Logger LOG = Logger.getLogger(Schema50.class);

    @Override
    public void execute(JdbcTemplate template) {

        if (template.queryForInt("select count(*) from version where version = 16") == 0) {
            LOG.info("Updating database schema to version 16.");
            template.execute("insert into version values (16)");

            for (String format : Arrays.asList("avi", "mpg", "mpeg", "mp4", "m4v", "mkv", "mov", "wmv")) {
                template.execute("insert into transcoding values(null,'" + format + " > flv' ,'" + format + "' ,'flv','ffmpeg -i %s -b %bk -ar 44100 -deinterlace -v 0 -f flv -',null,null,true,true)");
                template.execute("insert into player_transcoding select p.id as player_id, t.id as transaction_id from player p, transcoding t where t.name = '" + format + " > flv'");
            }
            LOG.info("Created video transcoding configuration.");
        }
    }
}