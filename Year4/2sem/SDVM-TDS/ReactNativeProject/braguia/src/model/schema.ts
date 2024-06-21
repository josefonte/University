import { appSchema, tableSchema } from '@nozbe/watermelondb';

const schema = appSchema({
  version: 6,
  tables: [
    tableSchema({
      name: 'users',
      columns: [
        { name: 'userType', type: 'string' },
        { name: 'last_login', type: 'string' },
        { name: 'is_superuser', type: 'boolean' },
        { name: 'username', type: 'string' },
        { name: 'first_name', type: 'string' },
        { name: 'last_name', type: 'string' },
        { name: 'email', type: 'string' },
        { name: 'is_staff', type: 'boolean' },
        { name: 'is_active', type: 'boolean' },
        { name: 'date_joined', type: 'string' },
        { name: 'historico', type: 'string' },
        { name: 'favorites', type: 'string' },
      ],
    }),
    tableSchema({
      name: 'trails',
      columns: [
        { name: 'trail_id', type: 'number' },
        { name: 'trail_name', type: 'string' },
        { name: 'trail_desc', type: 'string' },
        { name: 'trail_duration', type: 'number' },
        { name: 'trail_difficulty', type: 'string' },
        { name: 'trail_img', type: 'string' },
      ],
    }),
    tableSchema({
      name: 'edges',
      columns: [
        { name: 'edge_id', type: 'number' },
        { name: 'edge_transport', type: 'string' },
        { name: 'edge_duration', type: 'number' },
        { name: 'edge_desc', type: 'string' },
        { name: 'edge_trail', type: 'number', isIndexed: true },
        { name: 'edge_start_id', type: 'string', isIndexed: true },
        { name: 'edge_end_id', type: 'string', isIndexed: true },
      ],
    }),
    tableSchema({
      name: 'pins',
      columns: [
        { name: 'pin_id', type: 'number' },
        { name: 'pin_name', type: 'string' },
        { name: 'pin_desc', type: 'string' },
        { name: 'pin_lat', type: 'number' },
        { name: 'pin_lng', type: 'number' },
        { name: 'pin_alt', type: 'number' },
        { name: 'pin_trail', type: 'number', isIndexed: true },
      ],
    }),
    tableSchema({
      name: 'media',
      columns: [
        { name: 'media_id', type: 'number' },
        { name: 'media_file', type: 'string' },
        { name: 'media_type', type: 'string' },
        { name: 'media_pin', type: 'number', isIndexed: true },
      ],
    }),
    tableSchema({
      name: 'related_trails',
      columns: [
        { name: 'value', type: 'string' },
        { name: 'attrib', type: 'string' },
        { name: 'rel_trail_id', type: 'number', isIndexed: true },
      ],
    }),
    tableSchema({
      name: 'related_pins',
      columns: [
        { name: 'value', type: 'string' },
        { name: 'attrib', type: 'string' },
        { name: 'rel_pin_id', type: 'number', isIndexed: true },
      ],
    }),
    tableSchema({
      name: 'socials',
      columns: [
        { name: 'social_name', type: 'string' },
        { name: 'social_url', type: 'string' },
        { name: 'social_share_link', type: 'string' },
        { name: 'social_app', type: 'string' },
      ],
    }),
    tableSchema({
      name: 'contacts',
      columns: [
        { name: 'contact_name', type: 'string' },
        { name: 'contact_phone', type: 'string' },
        { name: 'contact_url', type: 'string' },
        { name: 'contact_mail', type: 'string' },
        { name: 'contact_desc', type: 'string' },
        { name: 'contact_app', type: 'string' },
      ],
    }),
    tableSchema({
      name: 'partners',
      columns: [
        { name: 'partner_name', type: 'string' },
        { name: 'partner_phone', type: 'string' },
        { name: 'partner_url', type: 'string' },
        { name: 'partner_mail', type: 'string' },
        { name: 'partner_desc', type: 'string' },
        { name: 'partner_app', type: 'string' },
      ],
    }),
    tableSchema({
      name: 'app',
      columns: [
        { name: 'app_name', type: 'string' },
        { name: 'app_desc', type: 'string' },
        { name: 'app_landing', type: 'string' },
      ],
    }),
  ],
});

export default schema;
